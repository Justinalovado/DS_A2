package driver;

import GUI.DrawMode;
import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ClientInterface;
import Interface.ManagerInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Manager extends UnicastRemoteObject implements ManagerInterface, BroadCaster {
//    private List<ClientInterface> clients = new ArrayList<>();
    private Map<String, ClientInterface> clients = new ConcurrentHashMap<>(); // TODO: Make method sync
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
        executor.submit(() -> {
            clients.forEach((clientName, clientInterface) -> {
                try {
                    clientInterface.updateAppendChat(name, msg);
                } catch (RemoteException e) {
                    System.out.println("A remote error caught by server, removing client: " + clientName);
                    kickClient(clientName);
                }
            });
        });
    }

    @Override
    public void broadcastUserList(DefaultListModel<String> lst) {
        executor.submit(()->{
            clients.forEach((clientName, clientInterface) -> {
                try {
                    clientInterface.updateUserList(lst);
                } catch (RemoteException e) {
                    // Handle exception, perhaps by removing the client
                    System.out.println("A remote error caught by server, removing client: " + clientName);
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
                    kickClient(clientName);
                }
            });
        });
    }

    @Override
    public void broadcastDrawTxt(Point a, Color c, String txt) {
        executor.submit(() -> {
            clients.forEach((clientName, clientInterface) -> {
                try {
                    clientInterface.updateDrawTxt(a, c, txt);
                } catch (RemoteException e) {
                    // Handle exception, perhaps by removing the client
                    System.out.println("A remote error caught by server, removing client: " + clientName);
                    kickClient(clientName);
                }
            });
        });
    }

    @Override
    public void broadcastOverhaulBoard(BufferedImage img) {
        executor.submit(() -> {
            clients.forEach((clientName, clientInterface) -> {
                try {
                    byte[] imgByte = serializeImage(img);
                    clientInterface.updateOverhaulBoard(imgByte);
                } catch (RemoteException e) {
                    // Handle exception, perhaps by removing the client
                    System.out.println("A remote error caught by server, removing client: " + clientName);
                    kickClient(clientName);
                }
            });
        });
    }

    private byte[] serializeImage(BufferedImage img){
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(img, "png", baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            System.out.println("Error on serializing bufferedImage");
            return null;
        }
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

    @Override
    public void clientDrawTxt(Point p, Color c, String txt) throws RemoteException {
        gui.whiteBoard.updateDrawTxt(p, c, txt);
        broadcastDrawTxt(p, c, txt);
    }

    public void kickClient(String name){
        executor.submit(() -> {
            gui.listPane.removeUser(name);
            ClientInterface client = clients.get(name);
            if (client != null){
                try{
                    client.kickedByManager();
                } catch (RemoteException e){
                    System.out.println("Tried to inform kicked user but failed");
                }
            }
            clients.remove(name);
        });
    }

    public void notifyShutdown(){
        clients.forEach((clientName, clientInterface) -> {
            try {
                clientInterface.managerShutdown();
            } catch (RemoteException e) {
                // Handle exception, perhaps by removing the client
                System.out.println("A remote error caught by server, removing client: " + clientName);
                kickClient(clientName);
            }
        });
    }
}
