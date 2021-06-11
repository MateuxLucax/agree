package gui;

// Where the user actually reads and writes messages

import data.GroupDataAccess;
import models.group.Group;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

public class GroupPanel {

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JLabel lbName;
    private JLabel lbLastMessage;
    private JButton btGoBack;

    private static final GroupDataAccess groupDataAccess = GroupDataAccess.getInstance();

    public GroupPanel(Group group) {
        groupDataAccess.populateUsers(group);
        groupDataAccess.loadMostRecentMessages(group);

        lbName = new JLabel(group.getName());
        lbLastMessage = new JLabel(group.getLastMessageDate().toString());
        btGoBack = new JButton("Go back");

        headerPanel = new JPanel();
        headerPanel.add(lbName);
        headerPanel.add(lbLastMessage);
        headerPanel.add(btGoBack);

        var messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.PAGE_AXIS));
        for (var msg : group.getMessages())
            messagesPanel.add(new MessagePanel(msg).getPanel());
        var messagesScrollPane = new JScrollPane();
        messagesScrollPane.setViewportView(messagesPanel);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(messagesScrollPane, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.PAGE_START);

    }

    public void setGoBackListener(ActionListener onGoBack) {
        btGoBack.addActionListener(onGoBack);
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}
