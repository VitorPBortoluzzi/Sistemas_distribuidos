4. Adaptação das Classes de UI (Servidor e Cliente)
Você precisa remover toda a lógica de ServerSocket, Socket, ObjectOutputStream e ObjectInputStream e substituí-la pela lógica RMI, além de incluir o Timer para chamadas periódicas.

A. Adaptação em ServidorJogador1.java (Servidor)
    Remova ServerSocket servidor, Socket socket_jogador2, ObjectOutputStream saida, ObjectInputStream entrada.

    Adicione o binding RMI no construtor.

    Remova o bloco new Thread() de conexão e leitura contínua de sockets.

    Adicione métodos para obter e atualizar componentes (para uso do ServidorImpl).

```java
// Dentro de ServidorJogador1.java
// ...
public class ServidorJogador1 extends javax.swing.JFrame {
    
    private IServidor servidorRemoto; // Objeto de referência para o ServidorImpl
    // ...
    
    public ServidorJogador1() {
        initComponents();
        
        // 1. Inicia o RMI (substitui a lógica de Socket)
        iniciarServidorRMI();
        
        // A lógica de recebimento do J2 agora está no ServidorImpl (método atualizarJogo)
        // A lógica de atualização do J2 (callback) está no ServidorImpl (método notificarFrutaParaCliente)
    }

    private void iniciarServidorRMI() {
        try {
            // Cria e registra o objeto remoto do Servidor
            servidorRemoto = new ServidorImpl(this); // 'this' é a referência para a UI
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Naming.rebind("rmi://localhost/ServidorCobra", servidorRemoto);
            System.out.println("Servidor RMI em execução.");
        } catch (Exception e) {
            System.out.println("Erro RMI no Servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // CHAMADO PELO ServidorImpl (que recebeu de J2)
    public void atualizarJogador2(Componente c) {
        jButtonJogador2.setBounds(c.x, c.y, c.largura, c.altura);
        // O ServidorImpl trata a atualização de J1 e Fruta para J2
    }
    
    // CHAMADO PELO ServidorImpl para obter o estado de J1 (para retorno ao J2)
    public Componente getComponenteJogador1() {
        return new Componente(jButtonJogador1.getBounds().x, 
                              jButtonJogador1.getBounds().y, 
                              jButtonJogador1.getBounds().width, 
                              jButtonJogador1.getBounds().height);
    }
    
    // CHAMADO PELO ServidorImpl para obter o estado da Fruta (para retorno ao J2)
    public Componente getComponenteFruta() {
        return new Componente(jButtonFruta.getBounds().x, 
                              jButtonFruta.getBounds().y, 
                              jButtonFruta.getBounds().width, 
                              jButtonFruta.getBounds().height);
    }

    private void jButtonFrutaKeyPressed(java.awt.event.KeyEvent evt) {
        // ... (lógica de movimento do J1 é a mesma)

        if (Movimenta.pegou(jButtonJogador1, jButtonFruta)) {
            // ... (lógica de posicionaAleatorio é a mesma)

            // NOVO: Usa o callback para notificar a nova fruta ao J2
            try {
                Componente cFruta = getComponenteFruta();
                ((ServidorImpl)servidorRemoto).notificarFrutaParaCliente(cFruta);
            } catch (RemoteException e) {
                System.out.println("Erro ao notificar fruta ao cliente via RMI.");
            }
        }
        
        // NOVO: A posição de J1 NÃO precisa ser enviada aqui. 
        // Ela será retornada quando J2 chamar atualizarJogo().
        // Você pode remover o bloco try/catch final que envia o jogador 1.
    }
    // ...
}

```

B. Adaptação em ClienteJogador2.java (Cliente)
    Remova Socket socket_jogador1, ObjectOutputStream saida, ObjectInputStream entrada.

    Adicione o lookup RMI e o binding do objeto de callback.

    Remova o bloco new Thread() de conexão e leitura contínua de sockets.

    Adicione um Timer para chamar periodicamente o método atualizarJogo() no servidor (a cada, digamos, 50ms para ser responsivo).


```java
// Dentro de ClienteJogador2.java
// ...
public class ClienteJogador2 extends javax.swing.JFrame {
    
    private IServidor servidorRemoto;
    private ICliente clienteCallback;
    private Timer timer; // Para chamadas periódicas ao servidor
    
    // ...
    public ClienteJogador2() {
        initComponents();
        
        // 1. Inicia o RMI (substitui a lógica de Socket)
        iniciarClienteRMI();
        
        // 2. Inicia o Timer para atualizar o estado do jogo
        iniciarTimerAtualizacao(50); // Chamadas a cada 50ms (20 vezes por segundo)
    }

    private void iniciarClienteRMI() {
        try {
            // Lookup do objeto remoto do Servidor
            servidorRemoto = (IServidor) Naming.lookup("rmi://localhost/ServidorCobra");
            
            // Cria e registra o objeto remoto do Cliente (Callback)
            clienteCallback = new ClienteImpl(this);
            servidorRemoto.registrarCliente(clienteCallback);
            
            System.out.println("Conexão RMI estabelecida e callback registrado.");
        } catch (Exception e) {
            System.out.println("Erro RMI no Cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void iniciarTimerAtualizacao(int periodoMs) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 1. Monta o componente J2 a ser enviado
                    Componente cJ2 = new Componente(jButtonJogador2.getBounds().x, 
                                                    jButtonJogador2.getBounds().y, 
                                                    jButtonJogador2.getBounds().width, 
                                                    jButtonJogador2.getBounds().height);
                    cJ2.tipo = Componente.JOGADOR;
                    
                    // 2. Chama o método remoto e recebe a resposta [J1, Fruta]
                    Componente[] estadoJogo = servidorRemoto.atualizarJogo(cJ2);
                    
                    // 3. Atualiza a UI do cliente com os dados recebidos
                    atualizarJogador1(estadoJogo[0]);
                    atualizarFruta(estadoJogo[1]);
                    
                } catch (Exception e) {
                    // System.out.println("Erro na chamada RMI periódica: " + e.getMessage());
                    // timer.cancel(); // Parar o jogo em caso de erro de conexão
                }
            }
        }, 0, periodoMs);
    }
    
    // CHAMADO PELO Timer (que recebeu do servidor)
    public void atualizarJogador1(Componente c) {
        jButtonJogador1.setBounds(c.x, c.y, c.largura, c.altura);
    }
    
    // CHAMADO PELO Timer OU PELO ServidorImpl (via callback)
    public void atualizarFruta(Componente c) {
        jButtonFruta.setBounds(c.x, c.y, c.largura, c.altura);
    }

    private void jButtonFrutaKeyPressed(java.awt.event.KeyEvent evt) {
        // ... (lógica de movimento do J2 é a mesma)
        
        if (Movimenta.pegou(jButtonJogador2, jButtonFruta)) {
            // O Cliente não reposiciona a fruta. A fruta é reposicionada
            // no Servidor e a nova posição será recebida na próxima 
            // chamada RMI ou via callback do Servidor (se J1 pegou).
        }
        
        // NOVO: A posição de J2 NÃO precisa ser enviada aqui. 
        // Ela será enviada na próxima iteração do Timer.
        // Você pode remover o bloco try/catch final que envia o jogador 2.
    }
    // ...
}
```