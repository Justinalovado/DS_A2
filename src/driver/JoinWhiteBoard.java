package driver;

import GUI.MainGUI;

import javax.swing.*;
import java.rmi.RemoteException;

public class JoinWhiteBoard {
    public static MainGUI gui;
    public static Client client;
    private static String name = "User1";
    public static void main(String[] args) {
        addShutdownCleaner();
        try {

            gui = new MainGUI("driver.Client", false); //TODO: change to input name
            SwingUtilities.invokeLater(() -> gui.setVisible(true));

            client = new Client(gui, name);
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
                    } catch (RemoteException | NullPointerException e) {
                        System.out.println("Error on disconnect");
                    }
                }
            }
        });
    }
}
