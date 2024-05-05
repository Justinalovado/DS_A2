package driver;

import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ClientInterface;
import Interface.ManagerInterface;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInterface, BroadCaster {

    private MainGUI gui;
    private ManagerInterface manager;
    private String name;
    public Client(MainGUI gui, ManagerInterface manager, String name) throws RemoteException {
        super();
        this.gui = gui;
        this.manager = manager;
        this.name = name;
    }

    @Override
    public void replyHello(String msg) throws RemoteException {
        System.out.println(msg);
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
    public void broadcastUserList(DefaultListModel<String> lst) {}

    @Override
    public void updateUserList(DefaultListModel<String> lst) throws RemoteException {
        gui.listPane.updateUserList(lst);
    }

    @Override
    public String getName() {
        return name;
    }
}
