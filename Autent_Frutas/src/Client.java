// import java.rmi.Naming;
// import java.util.UUID;

// public class Client {

//     private static final String CLIENTE_ID = "Cliente-" + UUID.randomUUID().toString().substring(0, 8);

//     public static void main(String args[]) {
//         try {
//             IAut d = (IAut) Naming.lookup("rmi://localhost/Aut");    
//             System.out.println("Conectado ao servidor como ID: " + CLIENTE_ID);
//             System.out.println("Solicitando fruta...");

//              // 2. Passa o CLIENTE_ID como argumento no método remoto
//             String frutaSorteada = d.pegaFrutinha(CLIENTE_ID);
            
//             System.out.println("Fruta sorteada recebida: " + frutaSorteada);

//         } catch (Exception e) {
//             System.out.println("Error: " + e);
//         }
//     }
// }
import java.util.Date; // Para a classe Date
import java.text.SimpleDateFormat;
import java.rmi.Naming;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;
import java.rmi.RemoteException;

public class Client {

    // Identificador único para este cliente
    private static final String CLIENTE_ID = "Cliente-" + UUID.randomUUID().toString().substring(0, 8);
    private static final String RMI_URL = "rmi://localhost/Aut";
    private static final long PERIODO_MS = 30000; // 30 segundos

    public static void main(String args[]) {
        try {
            // 1. O Lookup é feito apenas uma vez para obter a referência do objeto remoto
            final IAut objetoRemoto = (IAut) Naming.lookup(RMI_URL);

            System.out.println("Conectado ao servidor como ID: " + CLIENTE_ID);
            
            // Cria e agenda a tarefa periódica
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        // 2. A requisição remota é feita dentro do loop do Timer
                        String frutaSorteada = objetoRemoto.pegaFrutinha(CLIENTE_ID);
                        
                        // Atualiza o console (simulando a atualização do "código")
                        System.out.println("Fruta sorteada recebida [" + 
                                           new SimpleDateFormat("HH:mm:ss").format(new Date()) + 
                                           "]: " + frutaSorteada);
                        
                    } catch (RemoteException e) {
                        System.err.println("Erro na comunicação RMI. O servidor pode ter caído: " + e.getMessage());
                        // Pode-se optar por cancelar o Timer aqui se o erro for persistente
                        // timer.cancel();
                    } catch (Exception e) {
                        System.err.println("Erro inesperado: " + e);
                    }
                }
            };

            // Agenda a tarefa para começar imediatamente (0ms) e repetir a cada 30 segundos (30000ms)
            System.out.println("Iniciando requisições periódicas a cada " + (PERIODO_MS / 1000) + " segundos...");
            timer.scheduleAtFixedRate(task, 0, PERIODO_MS);

        } catch (Exception e) {
            System.err.println("Erro fatal ao conectar ao servidor RMI: " + e);
            e.printStackTrace();
        }
    }
}