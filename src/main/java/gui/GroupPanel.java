package gui;

// Where the user actually reads and writes messages

import models.group.Group;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageRepositoryTest;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class GroupPanel {

    private JPanel mainPanel;
    private JPanel messageListPanel;

    IMessageRepository msgRepo;

    public GroupPanel(Group group) {
        msgRepo = new MessageRepositoryTest();
        LinkedList<Message> messages = group.getMessages();

        /* mainPanel
         * \--- headerPanel (name, lastMessageDate)
         * \--- messageScrollPane
         *      \--- messagePanel
         *           \--- btLoadPrevious
         *           \--- messageListPanel
         *           \--- btLoadNewer
         */

        var lbName = new JLabel(group.getName());
        var lbLastMessageDate = new JLabel(messages.getLast().sentAt().toString());

        var headerPanel = new JPanel();
        headerPanel.add(lbName);
        headerPanel.add(lbLastMessageDate);

        messageListPanel = new JPanel();
        messageListPanel.setLayout(new BoxLayout(messageListPanel, BoxLayout.PAGE_AXIS));
        reloadMessageList(group);

        var btLoadPrevious = new JButton("Load previous messages");
        btLoadPrevious.addActionListener(evt -> {
            msgRepo.getMessagesBefore(group, messages.getFirst().sentAt());
            reloadMessageList(group);
        });

        var btLoadNewer = new JButton("Load newer messages");
        btLoadNewer.addActionListener(evt -> {
            msgRepo.getMessagesAfter(group, messages.getLast().sentAt());
            reloadMessageList(group);
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

    public void reloadMessageList(Group group) {
        messageListPanel.removeAll();
        for (var msg : group.getMessages())
            messageListPanel.add(new MessagePanel(msg).getJPanel());
        messageListPanel.revalidate();
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
