package driver;

import GUI.DrawMode;
import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ClientInterface;
import Interface.ManagerInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Manager extends UnicastRemoteObject implements ManagerInterface, BroadCaster {
//    private List<ClientInterface> clients = new ArrayList<>();
    private Map<String, ClientInterface> clients = new ConcurrentHashMap<>(); // TODO: swap to concurrent hashmap + sync method
    private MainGUI gui;

    public static Manager manager;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public Manager(MainGUI gui) throws RemoteException {
        super();
        this.gui = gui;
        manager = this;
    }

    @Override
    public String requestJoin(ClientInterface client) throws RemoteException {
        String clientName = client.getName();
        if (!clients.containsKey(clientName)){
            clients.put(clientName, client);
            gui.listPane.appendUser(clientName);
            return "Welcome";
        } else {
            return "Duplicate Name, try another";
        }
        // TODO: add manager confirmation on join
    }


    @Override
    public void broadcastChatAppend(String name, String msg) {
        // TODO: let executor run with a thread
        executor.submit(() -> {
            clients.forEach((clientName, clientInterface) -> {
                try {
                    clientInterface.updateAppendChat(name, msg);
//                    clientInterface.getRemoteError();
                } catch (RemoteException e) {
                    // Handle exception, perhaps by removing the client
                    System.out.println("A remote error caught by server, removing client: " + clientName);
//                    clients.remove(clientName);
//                    gui.listPane.removeUser(clientName);
                    kickClient(clientName);
                }
            });
        });
    }

    @Override
    public void broadcastUserList(DefaultListModel<String> lst) {
        // TODO: let executor run with a thread
        executor.submit(()->{
            clients.forEach((clientName, clientInterface) -> {
                try {
                    clientInterface.updateUserList(lst);
                } catch (RemoteException e) {
                    // Handle exception, perhaps by removing the client
                    System.out.println("A remote error caught by server, removing client: " + clientName);
//                    clients.remove(clientName);
//                    gui.listPane.removeUser(clientName);
                    kickClient(clientName);
                }
            });
        });
    }

    @Override
    public void broadcastDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) {
        executor.submit(() -> {
            clients.forEach((clientName, clientInterface) -> {
                try {
                    clientInterface.updateDrawShape(a, b, strokeWidth, color, shape);
                } catch (RemoteException e) {
                    // Handle exception, perhaps by removing the client
                    System.out.println("A remote error caught by server, removing client: " + clientName);
//                    clients.remove(clientName);
//                    gui.listPane.removeUser(clientName);
                    kickClient(clientName);
                }
            });
        });
    }

    /**
     * called from client, hits server, server update own chat interface &
     */
    @Override
    public void clientUpdateAppendChat(String name, String msg) throws RemoteException {
        gui.textPanel.appendChat(name, msg);
    }

    @Override
    public void clientQuit(ClientInterface client) throws RemoteException {
        String name = client.getName();
        gui.listPane.removeUser(name);
        clients.remove(name);
    }

    @Override
    public void clientDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) throws RemoteException {
        gui.whiteBoard.drawShape(a, b, strokeWidth, color, shape);
        broadcastDrawShape(a, b, strokeWidth, color, shape);
    }

    public void kickClient(String name){
        // TODO: let executor run
        ClientInterface client = clients.get(name);
        if (client != null){
            try{
                client.kickedByManager();
            } catch (RemoteException e){
                System.out.println("Tried to inform kicked user but failed");
            }
        }
        clients.remove(name);
    }
}
