package GUI;

import Interface.BroadCaster;
import driver.Announcer;
import driver.CreateWhiteBoard;
import driver.JoinWhiteBoard;

import javax.swing.*;
import java.awt.*;

public class ChatGUI extends JPanel{

    public JTextArea textArea;
    public JTextField textField;
    private boolean isManager;
    public ChatGUI(String name, boolean isManager){
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        add(textAreaScrollPane, BorderLayout.CENTER);

        textField = new JTextField();
        textField.addActionListener(e -> {
            String text = textField.getText();
            if (!text.isEmpty()) {
                appendChat(name, text);
                textField.setText("");
            }
        });
        add(textField, BorderLayout.SOUTH);
        textField.setPreferredSize(new Dimension(textField.getWidth(), textField.getPreferredSize().height));
        this.isManager = isManager;
    }

    /**
     *
     */
    public void appendChat(String username, String msg){
        if (isManager){
            textArea.append(username + ">" + msg + "\n");
        }
        Announcer.broadCaster.broadcastChatAppend(username, msg);
    }

    /**
     * for outer use, does not broadcast further
     */
    public void quiteAppendChat(String username, String msg){
        textArea.append(username + ">" + msg + "\n");
    }

}
