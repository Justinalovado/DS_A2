package GUI;

import driver.Announcer;
import driver.Client;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class MainGUI extends JFrame{

    public ChatGUI chatPanel;
    public ListGUI listPane;

    public WhiteBoardGUI whiteBoard;
    public JSplitPane splitPane;
    public JDialog waitDialog;

    public boolean isManager;

    public MainGUI(String name, boolean isManager){
        super(name);
        System.out.println(isManager);
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
        chatPanel = new ChatGUI(name, isManager);
        rightPanel.add(chatPanel);

        splitPane.setRightComponent(rightPanel);
        createWaitPane();
        this.isManager = isManager;
    }

    private void createWaitPane(){
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(this, "Waiting for Manager approval", true);
            dialog.setSize(200, 100);
            dialog.setLayout(new FlowLayout());

            JButton closeButton = new JButton("Abort & Close");
            closeButton.addActionListener(e -> System.exit(0));
            dialog.setLocationRelativeTo(this);
            dialog.add(closeButton);
            dialog.setAlwaysOnTop(true);
            this.waitDialog = dialog;
        });
    }

    public void promptKick(){
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"Exit", "Reconnect"};

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "You got kicked by Manager",
                    "Connection Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[1]
            );

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0); // Terminate the application
            } else if (choice == JOptionPane.NO_OPTION) {
                Client.client.reconnect();
            }
        });

    }


    public void promptJoinOutcome(boolean success, String msg){
        String title = success ? "Join Success" : "Join Failed";
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    msg,
                    title,
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (!success){
                Announcer.name = null;
                System.exit(0);
            }
        });
    }

    public void promptShutdownMessage(String msg){
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    msg,
                    "Shutting down",
                    JOptionPane.INFORMATION_MESSAGE
            );
            System.exit(0);
        });
    }

    public void promptWaiting(){
        SwingUtilities.invokeLater(() -> {
            waitDialog.setLocationRelativeTo(this);
            waitDialog.setVisible(true);
        });
    }

    public void unpromptWaiting(){
        SwingUtilities.invokeLater(() -> waitDialog.setVisible(false));
    }
}
