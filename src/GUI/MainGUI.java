package GUI;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame{
    public MainGUI(String name){
        super(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation((int) (getWidth() * 0.66));
        splitPane.setOneTouchExpandable(false);
        splitPane.setEnabled(false);
//        splitPane.setDividerSize(0); //thin separator
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // left panel for whiteboard
        JPanel whiteBoard = new WhiteBoardGUI();
        splitPane.setLeftComponent(whiteBoard);

        // right panel for list & chat
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));

        JPanel listPane = new ListGUI();
        rightPanel.add(listPane);

        // Bottom half of the right panel for text input
        JPanel textPanel = new ChatGUI();
        rightPanel.add(textPanel);

        splitPane.setRightComponent(rightPanel);

    }
}
