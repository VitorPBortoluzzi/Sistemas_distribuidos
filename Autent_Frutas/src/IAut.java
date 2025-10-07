import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAut extends Remote{  
    public String pegaFrutinha(String idClient) throws RemoteException;
}