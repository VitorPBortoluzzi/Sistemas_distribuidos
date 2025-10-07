import java.rmi.Naming;
import java.util.UUID;

public class Client {

    private static final String CLIENTE_ID = "Cliente-" + UUID.randomUUID().toString().substring(0, 8);

    public static void main(String args[]) {
        try {
            IAut d = (IAut) Naming.lookup("rmi://localhost/Aut");    
            System.out.println("Conectado ao servidor como ID: " + CLIENTE_ID);
            System.out.println("Solicitando fruta...");

             // 2. Passa o CLIENTE_ID como argumento no método remoto
            String frutaSorteada = d.pegaFrutinha(CLIENTE_ID);
            
            System.out.println("Fruta sorteada recebida: " + frutaSorteada);

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
