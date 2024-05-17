package driver;

import GUI.DrawMode;
import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ClientInterface;
import Interface.ManagerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Manager extends UnicastRemoteObject implements ManagerInterface, BroadCaster {
    private final Map<String, ClientInterface> clients = new ConcurrentHashMap<>(); // TODO: Make method sync
    private final MainGUI gui;

    public static Manager manager;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public Manager(MainGUI gui) throws RemoteException {
        super();
        this.gui = gui;
        manager = this;
    }

    /**
     * Server RMI interface, client calls this method to join the server
     * @param client
     * @return
     * @throws RemoteException
     */
    @Override
    public String clientRequestJoin(ClientInterface client) throws RemoteException {
        String clientName = client.getName();
        if (clients.containsKey(clientName) || clientName.equals(Utility.name)) {
            return "Duplicate Name, try another";
        } else {
            int response = JOptionPane.showConfirmDialog(gui,
                    "Do you want to allow " + clientName + " to join?",
                    "Confirm Join Request",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                clients.put(clientName, client);
                gui.listPane.appendUser(clientName);
                broadcastOverhaulBoard(gui.whiteBoard.getImg());
                broadcastOverhaulChat(gui.chatPanel.textArea.getText());
                broadcastSetLock(gui.whiteBoard.getDrawLock());
                return "Welcome";
            } else {
                return "Manager rejected your join request";
            }
        }
    }

    /**
     * update every client adding a new entry to chat record
     * @param name
     * @param msg
     */
    @Override
    public void broadcastChatAppend(String name, String msg) {
        executor.submit(() -> clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.updateAppendChat(name, msg);
            } catch (RemoteException e) {
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * update every client renewing the current online clients
     * @param lst
     */
    @Override
    public void broadcastUserList(DefaultListModel<String> lst) {
        executor.submit(()-> clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.updateUserList(lst);
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * update every client adding a new draw stroke
     * @param a
     * @param b
     * @param strokeWidth
     * @param color
     * @param shape
     */
    @Override
    public void broadcastDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) {
        executor.submit(() -> clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.updateDrawShape(a, b, strokeWidth, color, shape);
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * update every client adding a new text drawing
     * @param a
     * @param c
     * @param txt
     */
    @Override
    public void broadcastDrawTxt(Point a, Color c, String txt) {
        executor.submit(() -> clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.updateDrawTxt(a, c, txt);
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * update all client of the entire whiteboard
     * @param img
     */
    @Override
    public void broadcastOverhaulBoard(BufferedImage img) {
        executor.submit(() -> clients.forEach((clientName, clientInterface) -> {
            try {
                byte[] imgByte = Utility.serializeImage(img);
                if (img != null) {
                    clientInterface.updateOverhaulBoard(imgByte);
                }
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * update all client of the entire chat record
     * @param chat
     */
    @Override
    public void broadcastOverhaulChat(String chat) {
        executor.submit(() -> clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.updateOverhaulChat(chat);
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * notify all client of a new canvas update
     */
    @Override
    public void broadcastNewCanvas() {
        executor.submit(() -> clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.notifyNewCanvas();
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * notify all clients of a canvas closure
     */
    @Override
    public void broadcastCLoseCanvas() {
        executor.submit(() -> clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.notifyCloseCanvas();
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * update all client of canvas lock
     * @param bool
     */
    @Override
    public void broadcastSetLock(boolean bool) {
        executor.submit(() -> clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.updateCanvasLock(bool);
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        }));
    }

    /**
     * called from client, hits server, server update own chat interface &
     */
    @Override
    public void clientUpdateAppendChat(String name, String msg) throws RemoteException {
        gui.chatPanel.appendChat(name, msg);
    }

    /**
     * Server RMI for client to notify a quit
     * @param client
     * @throws RemoteException
     */
    @Override
    public void clientQuit(ClientInterface client) throws RemoteException {
        String name = client.getName();
        if (name == null) return;
        gui.listPane.removeUser(name);
        clients.remove(name);
    }

    /**
     * Server RMI interface for client to update their shape drawn
     * @param a
     * @param b
     * @param strokeWidth
     * @param color
     * @param shape
     * @throws RemoteException
     */
    @Override
    public void clientDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) throws RemoteException {
        gui.whiteBoard.drawShape(a, b, strokeWidth, color, shape);
        broadcastDrawShape(a, b, strokeWidth, color, shape);
    }

    /**
     * Server RMI interface for client to update their text drawn
     * @param p
     * @param c
     * @param txt
     * @throws RemoteException
     */
    @Override
    public void clientDrawTxt(Point p, Color c, String txt) throws RemoteException {
        gui.whiteBoard.updateDrawTxt(p, c, txt);
        broadcastDrawTxt(p, c, txt);
    }

    /**
     * Kicks selected client by name, intended for listGUI call
     * @param name
     */
    public void kickClient(String name){
        executor.submit(() -> {
            gui.listPane.removeUser(name);
            ClientInterface client = clients.get(name);
            if (client != null){
                try{
                    client.notifyKickedByManager();
                } catch (RemoteException e){
                    System.out.println("Tried to inform kicked user but failed");
                }
            }
            clients.remove(name);
        });
    }

    /**
     * Notify all client that server is shutdown.
     */
    public void notifyShutdown(){
        clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.notifyManagerShutdown();
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        });
    }
}
