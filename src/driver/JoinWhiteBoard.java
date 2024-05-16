package driver;

import GUI.MainGUI;

import javax.swing.*;
import java.rmi.RemoteException;

public class JoinWhiteBoard {
    public static MainGUI gui;
    public static Client client;

    public static void main(String[] args) {
        addShutdownCleaner();
        readInput(args);

        try {

            gui = new MainGUI(Announcer.name, false);
            SwingUtilities.invokeLater(() -> gui.setVisible(true));

            client = new Client(gui);
            Announcer.broadCaster = client;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readInput(String[] args){
        try {
            Announcer.name = args[2];
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Client name not provided, using default...");
            int randomInt = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000);
            Announcer.setDefaultName("Client" + randomInt);
        }

        try {
            Announcer.SESSION_PORT = Integer.parseInt(args[1]);
            Announcer.SESSION_IP = args[0];
            System.setProperty("java.rmi.server.hostname", Announcer.SESSION_IP);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
            System.out.println("Invalid arguments, using default...");
            Announcer.setDefaultSessionAddr();
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
