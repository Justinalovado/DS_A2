package GUI;

import driver.Announcer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuGUI extends JMenuBar {
    private WhiteBoardGUI board;
    public MenuGUI(WhiteBoardGUI board){
        this.board = board;
        JMenu fileMenu = new JMenu("File");

        JMenuItem New = new JMenuItem("New");
        New.addActionListener(this::handleNew);
        fileMenu.add(New);
        JMenuItem Open = new JMenuItem("Open");
        Open.addActionListener(this::handleOpen);
        fileMenu.add(Open);
        JMenuItem Save = new JMenuItem("Save");
        Save.addActionListener(this::handleSave);
        fileMenu.add(Save);
        JMenuItem SaveAs = new JMenuItem("SaveAs");
        SaveAs.addActionListener(this::handleSaveAs);
        fileMenu.add(SaveAs);
        JMenuItem Close = new JMenuItem("Close");
        Close.addActionListener(this::handleClose);
        fileMenu.add(Close);
        add(fileMenu);
    }

    private void handleClose(ActionEvent actionEvent) {
    }

    private void handleSaveAs(ActionEvent actionEvent) {
    }

    private void handleSave(ActionEvent actionEvent) {
    }

    private void handleOpen(ActionEvent actionEvent) {
    }

    private void handleNew(ActionEvent actionEvent) {
        board.initCanvas();
        board.repaint();
        Announcer.broadCaster.broadcastOverhaulBoard(board.getImg());
    }

}
