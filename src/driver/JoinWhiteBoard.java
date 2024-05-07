package driver;

import GUI.MainGUI;
import Interface.BroadCaster;
import Interface.ClientInterface;
import Interface.ManagerInterface;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinWhiteBoard {
    public static MainGUI gui;
    public static Client client;
    private static String name = "User1";
    public static void main(String[] args) {
        addShutdownCleaner();
        try {
//            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8080);

            gui = new MainGUI("driver.Client", false); //TODO: change to input name
            SwingUtilities.invokeLater(() -> gui.setVisible(true));

            // TODO: manager instantiation to be delegated to driver.Client object
//            ManagerInterface serverManager = (ManagerInterface) registry.lookup("driver.Manager");
            client = new Client(gui, name);
//            serverManager.requestJoin(client);
            Announcer.broadCaster = client;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addShutdownCleaner(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                if (client != null){
                    try {
                        client.disconnect();
                    } catch (RemoteException e) {
                        System.out.println("Remote error on disconnection");
                    }
                }
            }
        });
    }
}
