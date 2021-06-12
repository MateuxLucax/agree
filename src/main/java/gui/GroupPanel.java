package gui;

// Where the user actually reads and writes messages

import models.User;
import models.group.Group;
import repositories.message.IMessageRepository;
import repositories.message.MessageRepositoryTest;

import java.util.Date;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GroupPanel {

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JLabel lbName;
    private JLabel lbLastMessageDate;
    private JButton btGoBack;

    private IMessageRepository msgRepo = new MessageRepositoryTest();

    public GroupPanel(Group group) {
        // TODO groupRepo.getUsers(Group group);
        group.addUser(new User("foo", new Date()));
        group.addUser(new User("bar", new Date()));
        group.addUser(new User("aeiou", new Date()));

        msgRepo.getMostRecentMessages(group);

        lbName = new JLabel(group.getName());
        lbLastMessageDate = new JLabel(group.getLastMessageDate().toString());
        btGoBack = new JButton("Go back");

        headerPanel = new JPanel();
        headerPanel.add(lbName);
        headerPanel.add(lbLastMessageDate);
        headerPanel.add(btGoBack);

        var messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.PAGE_AXIS));
        for (var msg : group.getMessages())
            messagesPanel.add(new MessagePanel(msg).getJPanel());
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

    public JPanel getJPanel() {
        return mainPanel;
    }
}
