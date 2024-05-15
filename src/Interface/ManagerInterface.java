package Interface;

import GUI.DrawMode;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ManagerInterface extends Remote {
//    void SayHello(String msg) throws RemoteException;
    String clientRequestJoin(ClientInterface client) throws RemoteException;

    void clientUpdateAppendChat(String name, String msg) throws RemoteException;
    void clientQuit(ClientInterface client) throws RemoteException;
    void clientDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) throws RemoteException;
//    void testDrawShape(DrawMode shape) throws RemoteException;
    void clientDrawTxt(Point p, Color c, String txt) throws RemoteException;
}
