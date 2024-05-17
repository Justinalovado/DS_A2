package driver;

import GUI.DrawMode;
import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ClientInterface;
import Interface.ManagerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends UnicastRemoteObject implements ClientInterface, BroadCaster {

    public static Client client;
    private final MainGUI gui;
    private ManagerInterface manager;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    public Client(MainGUI gui) throws RemoteException {
        super();
        this.gui = gui;
        reconnect();
        client = this;
    }

    /**
     * lookup the RMI registry & tries to connect to server
     */
    public void reconnect(){
        try{
            Registry registry = LocateRegistry.getRegistry(Utility.SESSION_IP, Utility.SESSION_PORT);
            this.manager = (ManagerInterface) registry.lookup("driver.Manager");
            gui.unlockAll();
            requestJoin();
        } catch (RemoteException | NotBoundException e){
            gui.promptShutdownMessage("Cannot find Server");
        }
    }

    /**
     * Call to server RMI requesting to join, depending on server reply prompt result
     * @throws RemoteException
     */
    private void requestJoin() throws RemoteException {
        gui.promptWaiting();
        executor.submit(() -> {
            String outcome = null;
            try {
                outcome = manager.clientRequestJoin(this);
            } catch (RemoteException e) {
                gui.promptShutdownMessage("Lost server");
                return;
            }
            gui.unpromptWaiting();
            gui.promptJoinOutcome(outcome.equals("Welcome"), outcome);
        });
    }

    /**
     * Client RMI interface, for server to update client's chat interface
     * @param name
     * @param msg
     * @throws RemoteException
     */
    @Override
    public void updateAppendChat(String name, String msg) throws RemoteException {
        gui.chatPanel.quiteAppendChat(name, msg);
    }

    /**
     * Client notifies server of local new record on chat
     * @param name
     * @param msg
     */
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

    /**
     * client notifies server of local new drawn shape
     * @param a
     * @param b
     * @param strokeWidth
     * @param color
     * @param shape
     */
    @Override
    public void broadcastDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) {
        try {
            manager.clientDrawShape(a, b, strokeWidth, color, shape);
//            manager.testDrawShape(shape);
        } catch (RemoteException e) {
            System.out.println("A remote error caught by client");
        }
    }

    /**
     * client notifies server of local new drawn text
     * @param a
     * @param c
     * @param txt
     */
    @Override
    public void broadcastDrawTxt(Point a, Color c, String txt) {
        try {
            manager.clientDrawTxt(a, c, txt);
        } catch (RemoteException e) {
            System.out.println("A remote error caught by client");
        }
    }

    @Override
    public void broadcastOverhaulBoard(BufferedImage img) {
        // pass
    }

    @Override
    public void broadcastOverhaulChat(String chat) {
        // pass
    }

    @Override
    public void broadcastNewCanvas() {
        // pass
    }

    @Override
    public void broadcastCLoseCanvas() {
        // pass
    }

    @Override
    public void broadcastSetLock(boolean bool) {
        // pass
    }

    /**
     * Client RMI for server to update on new client list
     * @param lst
     * @throws RemoteException
     */
    @Override
    public void updateUserList(DefaultListModel<String> lst) throws RemoteException {
        gui.listPane.updateUserList(lst);
    }

    /**
     * Client RMI for server to update on a new draw stroke
     * @param a
     * @param b
     * @param strokeWidth
     * @param color
     * @param shape
     */
    @Override
    public void updateDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape) {
        gui.whiteBoard.drawShape(a, b, strokeWidth, color, shape);
    }

    /**
     * Client RMI for server to update on new draw text
     * @param a
     * @param c
     * @param txt
     * @throws RemoteException
     */
    @Override
    public void updateDrawTxt(Point a, Color c, String txt) throws RemoteException {
        gui.whiteBoard.updateDrawTxt(a, c, txt);
    }

    /**
     * Client RMI for server to update on entire canvas
     * @param imgByte
     * @throws RemoteException
     */
    @Override
    public void updateOverhaulBoard(byte[] imgByte) throws RemoteException {
        BufferedImage img = Utility.deserializeImage(imgByte);
        if (img != null){
            gui.whiteBoard.overhaulBoard(img);
        }
    }

    /**
     * Client RMI for server to update on entire chat history
     * @param chat
     * @throws RemoteException
     */
    @Override
    public void updateOverhaulChat(String chat) throws RemoteException {
        gui.chatPanel.textArea.setText(chat);
    }

    /**
     * Client RMI for server to update on canvas lock status
     * @param bool
     * @throws RemoteException
     */
    @Override
    public void updateCanvasLock(boolean bool) throws RemoteException {
        gui.whiteBoard.setDrawLock(bool);
    }

    /**
     * method to invoke a disconnection sequence
     * @throws RemoteException
     */
    public void disconnect() throws RemoteException {
        manager.clientQuit(this);
    }

    /**
     * Client RMI for server to notify a kick
     * @throws RemoteException
     */
    @Override
    public void notifyKickedByManager() throws RemoteException {
        gui.promptKick();
    }

    /**
     * Client RMI for server to notify a shutdown
     * @throws RemoteException
     */
    @Override
    public void notifyManagerShutdown() throws RemoteException {
        gui.promptShutdownMessage("Server is down, retry later");
    }

    /**
     * Client RMI for server to notify a new canvas setup
     * @throws RemoteException
     */
    @Override
    public void notifyNewCanvas() throws RemoteException {
        gui.promptNewCanvas();
        gui.whiteBoard.setDrawLock(false);
    }

    /**
     * Client RMI for server to notify a close canvas
     * @throws RemoteException
     */
    @Override
    public void notifyCloseCanvas() throws RemoteException {
        gui.whiteBoard.initCanvas();
        gui.whiteBoard.repaint();
        gui.promptCloseCanvas();
        gui.whiteBoard.setDrawLock(true);
    }

    /**
     * Client RMI for server debugging
     * @throws RemoteException
     */
    @Override
    public void getRemoteError() throws RemoteException { throw new RemoteException();}
    
    @Override
    public String getName() {
        return Utility.name;
    }
}
