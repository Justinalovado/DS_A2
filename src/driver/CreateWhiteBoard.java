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
    public static String name = "Manager";

    public static void main(String[] args) {
        addShutdownCleaner();

        readInput(args);

        // launching GUI
        MainGUI gui = new MainGUI(Announcer.name, true);
        SwingUtilities.invokeLater(() -> gui.setVisible(true));

        try {
            // launch handler
            manager = new Manager(gui);
            Registry registry = LocateRegistry.createRegistry(Announcer.SESSION_PORT);
            registry.bind("driver.Manager", manager);
            Announcer.broadCaster = manager;
        } catch (RemoteException | AlreadyBoundException e) {
            System.out.println("Something wrong when putting up server, perhaps port already Bound");
            System.exit(0);
        }
    }

    private static void readInput(String[] args){
        try {
            Announcer.name = args[2];
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Manager name not provided, using default...");
            Announcer.setDefaultName("Manager");
        }

        try {
            Announcer.SESSION_PORT = Integer.parseInt(args[1]);
            Announcer.SESSION_IP = args[0];
//            System.setProperty("java.rmi.server.hostname", Announcer.SESSION_IP);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
            System.out.println("Invalid arguments, using default...");
            Announcer.setDefaultSessionAddr();
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
