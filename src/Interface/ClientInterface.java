package Interface;

import GUI.DrawMode;

import javax.swing.*;
import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
//    void replyHello(String msg) throws RemoteException;
    void updateAppendChat(String name, String msg) throws RemoteException;
    void updateUserList(DefaultListModel<String> lst) throws RemoteException;
    void updateDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) throws RemoteException;
    void updateDrawTxt(Point a, Color c, String txt) throws RemoteException;
    void updateOverhaulBoard(byte[] imgByte) throws RemoteException;
    void updateOverhaulChat(String chat) throws RemoteException;
    void updateCanvasLock(boolean bool) throws RemoteException;
//    void disconnect() throws RemoteException;
    void notifyKickedByManager() throws RemoteException;
    void notifyManagerShutdown() throws RemoteException;
    void notifyNewCanvas() throws RemoteException;
    void notifyCloseCanvas() throws RemoteException;
    void getRemoteError() throws RemoteException;
    String getName() throws RemoteException;
}
