package gui;

// This is not where the user sees the group's messages and interacts with other users in it,
// but where he clicks to open the group

// TODO: add custom group icon on the left

import models.group.Group;

import javax.swing.*;
import java.awt.event.ActionListener;

public class GroupBar {

    private JPanel mainPanel;
    private JLabel lbName;
    private JLabel lbLastMessage;
    private JButton btOpen;

    public void setOpenListener(ActionListener onOpen) {
        btOpen.addActionListener(onOpen);
    }

    public GroupBar(Group group) {
        lbName.setText(group.getName());
        // TODO relative time: "last message 2 minutes ago"
        lbLastMessage.setText(group.getMessages().getFirst().sentAt().toString());
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
