package gui;

// Where the user actually reads and writes messages

import models.group.Group;

import javax.swing.*;
import java.awt.event.ActionListener;

public class GroupPanel {

    private JPanel mainPanel;
    private JLabel lbName;
    private JLabel lbLastMessage;
    private JButton btGoBack;

    public GroupPanel(Group group) {
        lbName = new JLabel(group.getName());
        lbLastMessage = new JLabel(group.getLastMessageDate().toString());
        btGoBack = new JButton("Go back");

        mainPanel = new JPanel();
        mainPanel.add(lbName);
        mainPanel.add(lbLastMessage);
        mainPanel.add(btGoBack);
    }

    public void setGoBackListener(ActionListener onGoBack) {
        btGoBack.addActionListener(onGoBack);
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}
