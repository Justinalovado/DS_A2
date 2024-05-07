package Interface;

import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
//    void replyHello(String msg) throws RemoteException;
    void updateAppendChat(String name, String msg) throws RemoteException;
    void updateUserList(DefaultListModel<String> lst) throws RemoteException;
//    void disconnect() throws RemoteException;
    void kickedByManager() throws RemoteException;
    String getName() throws RemoteException;
}
