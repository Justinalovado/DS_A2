package GUI;

import javax.swing.*;
import java.awt.*;

public class ChatGUI extends JPanel{
    public ChatGUI(){
        setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        add(textAreaScrollPane, BorderLayout.CENTER);

        JTextField textField = new JTextField();
        add(textField, BorderLayout.SOUTH);
        textField.setPreferredSize(new Dimension(textField.getWidth(), textField.getPreferredSize().height));
    }
}
