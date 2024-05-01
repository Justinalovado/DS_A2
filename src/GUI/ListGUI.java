package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ListGUI extends JPanel{

    private DefaultListModel<String> listModel;
    private JList<String> userList;

    private JButton kickBtn;

    public ListGUI(){
        setLayout(new BorderLayout());
        this.listModel = new DefaultListModel<>();
        this.listModel.addElement("User 1");
        this.listModel.addElement("User 2");

        this.userList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(userList);

        this.kickBtn = new JButton("Kick");
        kickBtn.addActionListener(this::kickUser);

        add(listScrollPane, BorderLayout.CENTER);
        add(kickBtn, BorderLayout.SOUTH);
    }

    public void kickUser(ActionEvent event){
        String selected = userList.getSelectedValue(); // Get selected item
        if (selected != null){
            listModel.removeElement(selected);
        }
    }
}
