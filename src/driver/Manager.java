package driver;

import GUI.ChatGUI;
import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ClientInterface;
import Interface.ManagerInterface;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class Manager extends UnicastRemoteObject implements ManagerInterface, BroadCaster {
    private List<ClientInterface> clients = new ArrayList<>();
    private MainGUI gui;


    public Manager(MainGUI gui) throws RemoteException {
        super();
        this.gui = gui;
    }

    @Override
    public boolean requestJoin(ClientInterface client) throws RemoteException {
        if (!clients.contains(client)){
            clients.add(client);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void SayHello(String msg) throws RemoteException {
        System.out.println(msg);
        for (ClientInterface c : clients){
            c.replyHello("Whatup");
        }
    }

    @Override
    public void broadcastChatAppend(String name, String msg) {
        for (ClientInterface client : clients){
            try {
                client.updateAppendChat(name, msg);
            } catch (RemoteException e) {
                // TODO: handle exception (kick?)
                System.out.println("A remote error caught by server");
            }
        }
    }

    /**
     * called from client, hits server, server update own chat interface &
     */
    @Override
    public void clientUpdateAppendChat(String name, String msg) throws RemoteException {
        gui.textPanel.appendChat(name, msg);
    }
}
