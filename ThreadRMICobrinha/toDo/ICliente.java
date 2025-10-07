import java.rmi.Remote;
import java.rmi.RemoteException;

//Esta interface permite que o servidor chame métodos no cliente para push de dados (atualização de estado).

// O servidor chamará este método no cliente para atualizar o estado do jogo
public interface ICliente extends Remote {
    
    /**
     * Recebe a posição do Jogador 1 (servidor)
     */
    public void receberPosicaoJogador1(Componente componenteJ1) throws RemoteException;
    
    /**
     * Recebe a nova posição da Fruta (usada quando J1 pega a fruta)
     */
    public void receberPosicaoFruta(Componente componenteFruta) throws RemoteException;
}