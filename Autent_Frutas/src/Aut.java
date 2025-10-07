import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Aut extends UnicastRemoteObject implements IAut {

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public Aut() throws RemoteException{
    }
    
    @Override
    public String pegaFrutinha(String idClient) throws RemoteException {     
        Fruta fruta = new Fruta();
        String frutaSorteada = fruta.sorteiaFruta();
        
        // Log de envio da fruta, incluindo o identificador do cliente
        String horaAtual = sdf.format(new Date());
        System.out.println("[" + horaAtual + "] Fruta '" + frutaSorteada + 
                           "' enviada para o Cliente ID: " + idClient);
                           
        return frutaSorteada;
    }   
}
