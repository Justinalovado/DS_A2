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

    private void requestJoin() throws RemoteException {
        gui.promptWaiting();
        executor.submit(() -> {
            String outcome = null;
            try {
                outcome = manager.clientRequestJoin(this);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            gui.unpromptWaiting();
            gui.promptJoinOutcome(outcome.equals("Welcome"), outcome);
        });
    }


    @Override
    public void updateAppendChat(String name, String msg) throws RemoteException {
        gui.chatPanel.quiteAppendChat(name, msg);
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

    @Override
    public void updateOverhaulBoard(byte[] imgByte) throws RemoteException {
        BufferedImage img = Utility.deserializeImage(imgByte);
        if (img != null){
            gui.whiteBoard.overhaulBoard(img);
        }
    }

    @Override
    public void updateOverhaulChat(String chat) throws RemoteException {
        gui.chatPanel.textArea.setText(chat);
    }

    @Override
    public void updateCanvasLock(boolean bool) throws RemoteException {
        gui.whiteBoard.setDrawLock(bool);
    }


    public void disconnect() throws RemoteException {
        manager.clientQuit(this);
    }

    @Override
    public void notifyKickedByManager() throws RemoteException {
        gui.promptKick();
    }

    @Override
    public void notifyManagerShutdown() throws RemoteException {
        gui.promptShutdownMessage("Server is down, retry later");
    }

    @Override
    public void notifyNewCanvas() throws RemoteException {
        gui.promptNewCanvas();
        gui.whiteBoard.setDrawLock(false);
    }

    @Override
    public void notifyCloseCanvas() throws RemoteException {
        gui.whiteBoard.initCanvas();
        gui.whiteBoard.repaint();
        gui.promptCloseCanvas();
        gui.whiteBoard.setDrawLock(true);
    }

    @Override
    public void getRemoteError() throws RemoteException { throw new RemoteException();}

    @Override
    public String getName() {
        return Utility.name;
    }
}
