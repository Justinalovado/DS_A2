package GUI;

import driver.Utility;
import driver.Client;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame{

    public ChatGUI chatPanel;
    public ListGUI listPane;

    public WhiteBoardGUI whiteBoard;
    public JDialog waitDialog;

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
        chatPanel = new ChatGUI(name, isManager);
        rightPanel.add(chatPanel);

        splitPane.setRightComponent(rightPanel);
        createWaitPane();
        this.isManager = isManager;
        this.whiteBoard.setDrawLock(!isManager);
        this.chatPanel.setChatLock(!isManager);
    }

    /**
     * helper function for creating a waiting dialog
     */
    private void createWaitPane(){
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(this, "Waiting for Manager approval", true);
            dialog.setSize(200, 100);
            dialog.setLayout(new FlowLayout());

            JButton closeButton = new JButton("Abort & Close");
            closeButton.addActionListener(e -> System.exit(0));
            dialog.setLocationRelativeTo(this);
            dialog.add(closeButton);
            this.waitDialog = dialog;
        });
    }

    /**
     * lift canvas and chat lock
     */
    public void unlockAll(){
        this.chatPanel.setChatLock(false);
        this.chatPanel.setChatLock(false);
    }

    /**
     * For client, prompt client of a kick from server
     */
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

    /**
     * for Client, prompt the joinRequest outcome
     * @param success
     * @param msg
     */
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
                Utility.name = null;
                System.exit(0);
            }
        });
    }

    /**
     * for Client, prompt a shutdown message
     * @param msg
     */
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

    /**
     * for Client, prompt a waiting panel for quitting program while waiting
     */
    public void promptWaiting(){
        SwingUtilities.invokeLater(() -> {
            waitDialog.setLocationRelativeTo(this);
            waitDialog.setVisible(true);
        });
    }

    /**
     * for Client, hide the waiting panel
     */
    public void unpromptWaiting(){
        SwingUtilities.invokeLater(() -> waitDialog.setVisible(false));
    }

    /**
     * for Client, prompt a new canvas update from server
     */
    public void promptNewCanvas(){
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                this,
                "Manager opened a new Canvas",
                "Canvas change",
                JOptionPane.INFORMATION_MESSAGE
        ));
    }

    /**
     * for Client, prompt a close canvas update from server
     */
    public void promptCloseCanvas(){
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                this,
                "Manager closed the canvas",
                "Canvas change",
                JOptionPane.INFORMATION_MESSAGE
        ));
    }
}
