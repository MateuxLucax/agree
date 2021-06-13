package gui;

// Where the user actually reads and writes messages

import models.User;
import models.group.Group;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageRepositoryTest;

import java.util.Date;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class GroupPanel {

    private JPanel mainPanel;
    private JButton btGoBack;

    private IMessageRepository msgRepo = new MessageRepositoryTest();

    public GroupPanel(Group group) {
        // TODO groupRepo.getUsers(Group group);
        group.addUser(new User("foo", new Date()));
        group.addUser(new User("bar", new Date()));
        group.addUser(new User("aeiou", new Date()));

        LinkedList<Message> messages = group.getMessages();

        msgRepo.getMostRecentMessages(group);

        /* mainPanel
         * \--- headerPanel (name, lastMessageDate, goBack)
         * \--- messageScrollPane
         *      \--- messagePanel
         *           \--- btLoadPrevious
         *           \--- messageListPanel
         *           \--- btLoadNewer
         */

        var lbName = new JLabel(group.getName());
        var lbLastMessageDate = new JLabel(messages.getLast().sentAt().toString());
        btGoBack = new JButton("Go back");

        var headerPanel = new JPanel();
        headerPanel.add(lbName);
        headerPanel.add(lbLastMessageDate);
        headerPanel.add(btGoBack);

        var messageListPanel = new JPanel();
        messageListPanel.setLayout(new BoxLayout(messageListPanel, BoxLayout.PAGE_AXIS));
        for (var msg : messages)
            messageListPanel.add(new MessagePanel(msg).getJPanel());

        var btLoadPrevious = new JButton("Load previous messages");
        btLoadPrevious.addActionListener(evt -> {
            msgRepo.getMessagesBefore(group, messages.getFirst().sentAt());
            messageListPanel.removeAll();
            for (var msg : messages)
                messageListPanel.add(new MessagePanel(msg).getJPanel());
            messageListPanel.revalidate();
        });

        var btLoadNewer = new JButton("Load newer messages");
        btLoadNewer.addActionListener(evt -> {
            msgRepo.getMessagesAfter(group, messages.getLast().sentAt());
            messageListPanel.removeAll();
            for (var msg : messages)
                messageListPanel.add(new MessagePanel(msg).getJPanel());
            messageListPanel.revalidate();
        });

        var messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.add(btLoadPrevious, BorderLayout.PAGE_START);
        messagePanel.add(messageListPanel, BorderLayout.CENTER);
        messagePanel.add(btLoadNewer, BorderLayout.PAGE_END);

        var messageScrollPane = new JScrollPane();
        messageScrollPane.setViewportView(messagePanel);
        messageScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.PAGE_START);
        mainPanel.add(messageScrollPane, BorderLayout.CENTER);
    }

    public void setGoBackListener(ActionListener onGoBack) {
        btGoBack.addActionListener(onGoBack);
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
