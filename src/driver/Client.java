package driver;

import GUI.DrawMode;
import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ClientInterface;
import Interface.ManagerInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInterface, BroadCaster {

    public static Client client;
    private MainGUI gui;
    private ManagerInterface manager;
    private String name;

    private String addr = "127.0.0.1";
    private int port = 8080;
    public Client(MainGUI gui, String name) throws RemoteException {
        super();
        this.gui = gui;
        this.name = name;
        reconnect();
        client = this;
    }

    public void reconnect(){
        try{
            Registry registry = LocateRegistry.getRegistry(addr, port);
            this.manager = (ManagerInterface) registry.lookup("driver.Manager");
            String outcome = manager.requestJoin(this);
            gui.promptJoinOutcome(outcome);
        } catch (RemoteException | NotBoundException e){
//            System.err.println(e.getMessage());
//            System.exit(0);
            gui.promptShutdownMessage("Cannot find Server");
        }
    }


    @Override
    public void updateAppendChat(String name, String msg) throws RemoteException {
        gui.textPanel.quiteAppendChat(name, msg);
    }

    @Override
    public void broadcastChatAppend(String name, String msg) {
        try{
            manager.clientUpdateAppendChat(name, msg);
        } catch (RemoteException e){
            System.out.println("A remote error caught by client");
        }
    }

    @Override
    public void broadcastUserList(DefaultListModel<String> lst) {} // client should not send list

    @Override
    public void broadcastDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) {
        try {
            manager.clientDrawShape(a, b, strokeWidth, color, shape);
//            manager.testDrawShape(shape);
        } catch (RemoteException e) {
            System.out.println("A remote error caught by client");
        }
    }

    @Override
    public void broadcastDrawTxt(Point a, Color c, String txt) {
        try {
            manager.clientDrawTxt(a, c, txt);
        } catch (RemoteException e) {
            System.out.println("A remote error caught by client");
        }
    }

    @Override
    public void updateUserList(DefaultListModel<String> lst) throws RemoteException {
        gui.listPane.updateUserList(lst);
    }

    @Override
    public void updateDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) {
        gui.whiteBoard.drawShape(a, b, strokeWidth, color, shape);
    }

    @Override
    public void updateDrawTxt(Point a, Color c, String txt) throws RemoteException {
        gui.whiteBoard.updateDrawTxt(a, c, txt);
    }

    public void disconnect() throws RemoteException {
        manager.clientQuit(this);
    }

    @Override
    public void kickedByManager() throws RemoteException {
        gui.promptKick();
    }

    @Override
    public void managerShutdown() throws RemoteException {
        gui.promptShutdownMessage("Server is down, retry later");
    }

    @Override
    public void getRemoteError() throws RemoteException { throw new RemoteException();}

    @Override
    public String getName() {
        return name;
    }
}
