package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void replyHello(String msg) throws RemoteException;
    void updateAppendChat(String name, String msg) throws RemoteException;
}
