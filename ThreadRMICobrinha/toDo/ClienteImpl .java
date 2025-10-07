/*
Cliente: ClienteImpl.java (Implementa ICliente)
Esta classe será o objeto remoto que o Cliente (ClienteJogador2) expõe para o servidor chamar (o mecanismo de callback).
*/
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JButton;

public class ClienteImpl extends UnicastRemoteObject implements ICliente {
    
    private ClienteJogador2 clienteUI;

    public ClienteImpl(ClienteJogador2 ui) throws RemoteException {
        this.clienteUI = ui;
    }

    @Override
    public void receberPosicaoJogador1(Componente componenteJ1) throws RemoteException {
        // Atualiza a posição do Jogador 1 (controlado pelo servidor) na UI do cliente
        clienteUI.atualizarJogador1(componenteJ1);
    }

    @Override
    public void receberPosicaoFruta(Componente componenteFruta) throws RemoteException {
        // Atualiza a posição da fruta na UI do cliente
        clienteUI.atualizarFruta(componenteFruta);
    }
}