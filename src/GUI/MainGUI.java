package GUI;

import driver.Client;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class MainGUI extends JFrame{

    public ChatGUI chatPanel;
    public ListGUI listPane;

    public WhiteBoardGUI whiteBoard;
    public JSplitPane splitPane;

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
        this.isManager = isManager;
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

    public JDialog promptWaiting(){
        Object[] options = {"Abort", "Dismiss"};

        JOptionPane optionPane = new JOptionPane(
                "Waiting...",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);

        final JDialog dialog = optionPane.createDialog(this, "Waiting");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        optionPane.addPropertyChangeListener(e -> {
            String prop = e.getPropertyName();
            if (dialog.isVisible() && (e.getSource() == optionPane)
                    && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                // Handle the selected option
                int value = (Integer) optionPane.getValue();
                if (value == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else if (value == JOptionPane.NO_OPTION) {
                    dialog.dispose();
                }
            }
        });

        SwingUtilities.invokeLater(() -> {
            dialog.pack();
            dialog.setVisible(true);
        });

        return dialog;
    }
}
