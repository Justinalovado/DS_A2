package Interface;

import GUI.DrawMode;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public interface BroadCaster {
    void broadcastChatAppend(String name, String msg);
    void broadcastUserList(DefaultListModel<String> lst);
    void broadcastDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape);
    void broadcastDrawTxt(Point a, Color c, String txt);
    void broadcastOverhaulBoard(BufferedImage img);
    void broadcastOverhaulChat(String chat);
    void broadcastNewCanvas();
    void broadcastCLoseCanvas();
    void broadcastSetLock(boolean bool);
}
