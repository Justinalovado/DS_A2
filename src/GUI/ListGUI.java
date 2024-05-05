package GUI;

import driver.CreateWhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ListGUI extends JPanel{

    private DefaultListModel<String> listModel;
    private JList<String> userList;

    private JButton kickBtn;
    private boolean isManager;

    // TODO: implement Kick functionality
    public ListGUI(boolean isManager){
        setLayout(new BorderLayout());
        this.listModel = new DefaultListModel<>();
//        this.listModel.addElement("User 1");
//        this.listModel.addElement("User 2");
        if (isManager) this.listModel.addElement("Manager");

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
        if (selected != null){
            listModel.removeElement(selected);
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
        System.out.println(isManager);
        if (isManager) {
            CreateWhiteBoard.manager.broadcastUserList(listModel);
        }
    }

    public void appendUser(String username){
        listModel.addElement(username);
        updateUserList();
    }
}
