import GUI.MainGUI;

import javax.swing.*;

public class CreateWhiteBoard {
    public static void main(String[] args) {
        MainGUI gui = new MainGUI("Manager");

        SwingUtilities.invokeLater(() -> gui.setVisible(true));
    }

}
