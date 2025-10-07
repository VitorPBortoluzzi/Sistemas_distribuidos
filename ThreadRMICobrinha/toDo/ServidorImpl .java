// Esta classe será o objeto remoto no lado do Servidor (ServidorJogador1).
// Servidor: ServidorImpl.java (Implementa IServidor)

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServidorImpl extends UnicastRemoteObject implements IServidor {
    
    private ServidorJogador1 servidorUI;
    private ICliente clienteCallback; // Objeto remoto do cliente para callback

    public ServidorImpl(ServidorJogador1 ui) throws RemoteException {
        this.servidorUI = ui;
    }
    
    @Override
    public void registrarCliente(ICliente clienteCallback) throws RemoteException {
        this.clienteCallback = clienteCallback;
        System.out.println("Cliente registrado para callbacks.");
    }

    @Override
    public Componente[] atualizarJogo(Componente componenteJ2) throws RemoteException {
        // 1. Atualiza a UI do Servidor com a posição do Jogador 2 (cliente)
        servidorUI.atualizarJogador2(componenteJ2);
        
        // 2. O servidor retorna o estado atual do J1 e da Fruta.
        // O servidor precisa ter métodos para obter esses dados da sua UI.
        Componente c1 = servidorUI.getComponenteJogador1();
        Componente cFruta = servidorUI.getComponenteFruta();
        
        return new Componente[] {c1, cFruta};
    }
    
    // Método auxiliar para o ServidorJogador1 chamar quando pegar a fruta e precisar atualizar o Cliente
    public void notificarFrutaParaCliente(Componente componenteFruta) throws RemoteException {
        if (clienteCallback != null) {
            clienteCallback.receberPosicaoFruta(componenteFruta);
        }
    }
}