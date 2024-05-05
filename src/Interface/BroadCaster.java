package Interface;

import javax.swing.*;

public interface BroadCaster {
    void broadcastChatAppend(String name, String msg);
    void broadcastUserList(DefaultListModel<String> lst);
}
