package GUI;

import driver.Utility;
import driver.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ListGUI extends JPanel{

    private DefaultListModel<String> listModel;
    private JList<String> userList;

    private final boolean isManager;

    public ListGUI(boolean isManager){
        setLayout(new BorderLayout());
        this.listModel = new DefaultListModel<>();
        if (isManager) this.listModel.addElement(Utility.name);

        this.userList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(userList);

        JButton kickBtn = new JButton("Kick");
        kickBtn.addActionListener(this::kickUser);

        add(listScrollPane, BorderLayout.CENTER);
        add(kickBtn, BorderLayout.SOUTH);
        this.isManager = isManager;
    }

    /**
     * call manager to kick a client
     * @param event
     */
    public void kickUser(ActionEvent event){
        String selected = userList.getSelectedValue();
        if (selected != null && isManager && !selected.equals(Utility.name)){
            listModel.removeElement(selected);
            Manager.manager.kickClient(selected);
            Manager.manager.broadcastUserList(listModel);
        }
    }


    /**
     * for Client, to refresh userlist updated from server
     */
    public void updateUserList(DefaultListModel<String> lst){
        listModel = lst;
        updateUserList();
    }

    /**
     * refresh display, sync with manager's client list
     */
    public void updateUserList(){
        userList.setModel(listModel);
        if (isManager) {
            Utility.broadCaster.broadcastUserList(listModel);
        }
    }

    /**
     * add user to display
     * @param username
     */
    public void appendUser(String username){
        listModel.addElement(username);
        updateUserList();
    }

    /**
     * remove user from display
     * @param username
     */
    public void removeUser(String username){
        listModel.removeElement(username);
        updateUserList();
    }
}
