package GUI;

import driver.Announcer;
import driver.CreateWhiteBoard;
import driver.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ListGUI extends JPanel{

    private DefaultListModel<String> listModel;
    private JList<String> userList;

    private JButton kickBtn;
    private boolean isManager;

    public ListGUI(boolean isManager){
        setLayout(new BorderLayout());
        this.listModel = new DefaultListModel<>();
        if (isManager) this.listModel.addElement(Announcer.name);

        this.userList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(userList);

        this.kickBtn = new JButton("Kick");
        kickBtn.addActionListener(this::kickUser);

        add(listScrollPane, BorderLayout.CENTER);
        add(kickBtn, BorderLayout.SOUTH);
        this.isManager = isManager;
    }

    public void kickUser(ActionEvent event){
        String selected = userList.getSelectedValue(); // Get selected item
        if (selected != null && isManager && !selected.equals(Announcer.name)){
            listModel.removeElement(selected);
            Manager.manager.kickClient(selected); // remove from existence & notify
            Manager.manager.broadcastUserList(listModel); // update all current user
        }
    }


    /**
     * on client side to refresh display
     */
    public void updateUserList(DefaultListModel<String> lst){
        listModel = lst;
        updateUserList();
    }

    public void updateUserList(){
        userList.setModel(listModel);
        if (isManager) {
            Announcer.broadCaster.broadcastUserList(listModel);
        }
    }

    public void appendUser(String username){
        listModel.addElement(username);
        updateUserList();
    }
    public void removeUser(String username){
        listModel.removeElement(username);
        updateUserList();
    }
}
