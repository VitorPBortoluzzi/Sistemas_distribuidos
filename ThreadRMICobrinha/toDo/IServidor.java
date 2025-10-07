import java.rmi.Remote;
import java.rmi.RemoteException;

// O cliente usará essa interface para interagir com o servidor.
public interface IServidor extends Remote{
    /**
     * O Jogador 2 (cliente) chama este método para:
     * 1. Enviar sua própria posição (componenteJ2).
     * 2. Receber o estado atual do J1 e da Fruta.
     * * @param componenteJ2 Posição atual do Jogador 2.
     * @return Array contendo [Componente J1, Componente Fruta].
     */
    public Componente[] atualizarJogo(Componente componenteJ2) throws RemoteException;
    
    /**
     * Registra o objeto remoto do Cliente (Callback) no Servidor
     * para que o Servidor possa chamá-lo.
     */
    public void registrarCliente(ICliente clienteCallback) throws RemoteException;
}