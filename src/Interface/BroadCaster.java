package Interface;

import GUI.DrawMode;

import javax.swing.*;
import java.awt.*;

public interface BroadCaster {
    void broadcastChatAppend(String name, String msg);
    void broadcastUserList(DefaultListModel<String> lst);
    void broadcastDrawShape(Point a, Point b, float strokeWidth, Color color, DrawMode shape);
}
