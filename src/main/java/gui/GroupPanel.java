package gui;

// Where the user actually reads and writes messages

import app.UserSession;
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

    private Group group;
    private JPanel mainPanel;
    private JPanel messageListPanel;

    public GroupPanel(Group group) {
        this.group = group;

        IMessageRepository msgRepo = UserSession.getInstance().getMessageRepository();

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
        reloadMessageList();

        var btLoadPrevious = new JButton("Load previous messages");
        btLoadPrevious.addActionListener(evt -> {
            msgRepo.getMessagesBefore(group, messages.getFirst().sentAt());
            reloadMessageList();
        });

        var btLoadNewer = new JButton("Load newer messages");
        btLoadNewer.addActionListener(evt -> {
            msgRepo.getMessagesAfter(group, messages.getLast().sentAt());
            reloadMessageList();
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

    public void reloadMessageList() {
        messageListPanel.removeAll();
        for (var msg : group.getMessages())
            messageListPanel.add(new MessagePanel(msg).getJPanel());
        messageListPanel.revalidate();
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
