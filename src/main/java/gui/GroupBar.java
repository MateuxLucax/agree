package gui;

// This is not where the user sees the group's messages and interacts with other users in it,
// but where he clicks to open the group

// TODO: add custom group icon on the left

import models.group.Group;

import javax.swing.*;

public class GroupBar {

    private JPanel main;
    private JLabel lbName;
    private JLabel lbLastMessage;

    public GroupBar(Group group) {
        lbName.setText(group.getName());
        // TODO relative time: "last message 2 minutes ago"
        lbLastMessage.setText(group.getLastMessageDate().toString());
    }

    public JPanel getPanel() {
        return main;
    }
}
