package GUI;

import driver.Announcer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MenuGUI extends JMenuBar {
    private WhiteBoardGUI board;
    private File curFile = null;
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


    private void handleNew(ActionEvent actionEvent) {
        this.curFile = null;
        board.initCanvas();
        board.repaint();
        Announcer.broadCaster.broadcastOverhaulBoard(board.getImg());
    }
    private void handleOpen(ActionEvent actionEvent) {
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(board) == JFileChooser.APPROVE_OPTION) {
                    File openFile = fileChooser.getSelectedFile();
                    curFile = openFile;
                    try {
                        return ImageIO.read(openFile);
                    } catch (IOException e) {
                        System.out.println("Failed to open file");
                        return null;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    BufferedImage loadedImg = get();
                    if (loadedImg != null) {
                        board.setImg(loadedImg);  // Assuming 'board' has a method setImage(BufferedImage)
                        board.repaint();
                        Announcer.broadCaster.broadcastOverhaulBoard(loadedImg);
                        JOptionPane.showMessageDialog(board, "Image Opened Successfully");
                    } else {
                        JOptionPane.showMessageDialog(board, "Open Failed");
                    }
                } catch (ExecutionException | InterruptedException e) {
                    JOptionPane.showMessageDialog(board, "Error during image open: " + e.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleSave(ActionEvent actionEvent) {
        if (curFile != null){
            SwingWorker<Boolean, Void> worker= new SwingWorker<Boolean, Void>(){
                @Override
                protected Boolean doInBackground() throws Exception {
                    BufferedImage img = board.getImg();
                    try{
                        if (!curFile.getName().toLowerCase().endsWith(".png")){
                            curFile = new File(curFile.getParentFile(), curFile.getName() + ".png");
                        }
                        return ImageIO.write(img,"PNG", curFile);
                    } catch (IOException e) {
                        System.out.println("Failed to save file");
                        return false;
                    }
                }

                @Override
                protected void done() {
                    try{
                        boolean status = get();
                        if (status){
                            JOptionPane.showMessageDialog(board, "Image Saved");
                        } else {
                            JOptionPane.showMessageDialog(board, "Save Failed");
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            worker.execute();
        } else {
            handleSaveAs(actionEvent);
        }
    }

    private void handleSaveAs(ActionEvent actionEvent) {
        SwingWorker<Boolean, Void> worker= new SwingWorker<Boolean, Void>(){
            @Override
            protected Boolean doInBackground() throws Exception {
                JFileChooser fileChooser = new JFileChooser();
                BufferedImage img = board.getImg();
                if (fileChooser.showSaveDialog(board) == JFileChooser.APPROVE_OPTION){
                    File saveFile = fileChooser.getSelectedFile();
                    try{
                        if (!saveFile.getName().toLowerCase().endsWith(".png")){
                            saveFile = new File(saveFile.getParentFile(), saveFile.getName() + ".png");
                        }
                        if (curFile == null){
                            curFile = saveFile;
                        }
                        return ImageIO.write(img,"PNG", saveFile);
                    } catch (IOException e) {
                        System.out.println("Failed to save file");
                        return false;
                    }
                }
                return false;
            }

            @Override
            protected void done() {
                try{
                    boolean status = get();
                    if (status){
                        JOptionPane.showMessageDialog(board, "Image Saved");
                    } else {
                        JOptionPane.showMessageDialog(board, "Save Failed");
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        worker.execute();
    }

    private void handleClose(ActionEvent actionEvent) {
    }

}
