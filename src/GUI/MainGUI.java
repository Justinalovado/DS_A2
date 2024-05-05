package GUI;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame{

    public ChatGUI textPanel;
    public ListGUI listPane;

    public WhiteBoardGUI whiteBoard;

    public boolean isManager;

    public MainGUI(String name, boolean isManager){
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
}
