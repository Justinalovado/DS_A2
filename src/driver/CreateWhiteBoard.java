package driver;

import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ManagerInterface;

import javax.swing.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CreateWhiteBoard {

    public static MainGUI gui;
    public static Manager manager;
//    public static BroadCaster broadCaster;

    public static void main(String[] args) {
        addShutdownCleaner();
        // launching GUI
        MainGUI gui = new MainGUI("driver.Manager", true);
        SwingUtilities.invokeLater(() -> gui.setVisible(true));

        try {
            // launch handler
            manager = new Manager(gui);
            Registry registry = LocateRegistry.createRegistry(8080);
            registry.bind("driver.Manager", manager);
//            broadCaster = manager;
            Announcer.broadCaster = manager;
        } catch (RemoteException | AlreadyBoundException e) {
            System.out.println("Something wrong when putting up server");
            throw new RuntimeException(e);
        }
    }
    private static void addShutdownCleaner(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                if (manager != null){
                    manager.notifyShutdown();
                }
            }
        });
    }
}
