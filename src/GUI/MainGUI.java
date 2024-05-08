package GUI;

import Interface.BroadCaster;
import driver.Client;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame{

    public ChatGUI textPanel;
    public ListGUI listPane;

    public WhiteBoardGUI whiteBoard;
    public JSplitPane splitPane;

    public boolean isManager;

    public MainGUI(String name, boolean isManager){
        super(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation((int) (getWidth() * 0.66));
        splitPane.setOneTouchExpandable(false);
        splitPane.setEnabled(false);
//        splitPane.setDividerSize(0); //thin separator
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // left panel for whiteboard
        whiteBoard = new WhiteBoardGUI(isManager);
        splitPane.setLeftComponent(whiteBoard);

        // right panel for list & chat
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));

        listPane = new ListGUI(isManager);
        rightPanel.add(listPane);

        // Bottom half of the right panel for text input
        textPanel = new ChatGUI(name, isManager);
        rightPanel.add(textPanel);

        splitPane.setRightComponent(rightPanel);
        this.isManager = isManager;
    }

    public void promptKick(){
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"Exit", "Reconnect"};

            int choice = JOptionPane.showOptionDialog(
                    splitPane,
                    "You got kicked by Manager",
                    "Connection Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0); // Terminate the application
            } else if (choice == JOptionPane.NO_OPTION) {
                Client.client.reconnect();
            }
        });

    }


    public void promptJoinOutcome(String outcome){
        SwingUtilities.invokeLater(() -> {
            if (outcome.equals("Welcome")){
                JOptionPane.showMessageDialog(
                        splitPane,
                        "Join successful",
                        outcome,
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        splitPane,
                        "Join fail",
                        outcome,
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }


}
