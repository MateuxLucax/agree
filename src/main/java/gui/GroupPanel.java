package gui;

// Where the user actually reads and writes messages

import models.group.Group;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageRepositoryTest;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.LinkedList;

public class GroupPanel {

    private JPanel mainPanel;
    private JPanel messageListPanel;

    IMessageRepository msgRepo;

    public GroupPanel(Group group, boolean showToOwner) {
        msgRepo = new MessageRepositoryTest();
        LinkedList<Message> messages = group.getMessages();

        /* mainPanel
         * \--- headerPanel (name, lastMessageDate[, manageGroup if owner])
         * \--- messagesScrollPane
         *      \--- messagesPanel
         *           \--- btLoadPrevious
         *           \--- messageListPanel
         *           \--- btLoadNewer
         */

        var lbName = new JLabel(group.getName());
        var headerPanel = new JPanel();
        headerPanel.add(lbName);

        if (!messages.isEmpty()) {
            var lbLastMessageDate = new JLabel(messages.getLast().sentAt().toString());
            headerPanel.add(lbLastMessageDate);
        }

        if (showToOwner) {
            JButton btManage = new JButton("Manage");
            headerPanel.add(btManage);
            // TODO actual group management panel (delete group, renaming group, removing users etc.)
        }

        messageListPanel = new JPanel();
        messageListPanel.setLayout(new BoxLayout(messageListPanel, BoxLayout.PAGE_AXIS));
        reloadMessageList(group);

        var btLoadPrevious = new JButton("Load previous messages");
        btLoadPrevious.addActionListener(evt -> {
            Date date = messages.isEmpty() ? new Date() : messages.getFirst().sentAt();
            msgRepo.getMessagesBefore(group, date);
            reloadMessageList(group);
        });

        var btLoadNewer = new JButton("Load newer messages");
        btLoadNewer.addActionListener(evt -> {
            Date date = messages.isEmpty() ? new Date() : messages.getLast().sentAt();
            msgRepo.getMessagesAfter(group, date);
            reloadMessageList(group);
            // TODO update "lbLastMessageDate" here
        });

        var messagesPanel = new JPanel();
        messagesPanel.setLayout(new BorderLayout());
        messagesPanel.add(btLoadPrevious, BorderLayout.PAGE_START);
        messagesPanel.add(messageListPanel, BorderLayout.CENTER);
        messagesPanel.add(btLoadNewer, BorderLayout.PAGE_END);

        var messagesScrollPane = new JScrollPane();
        messagesScrollPane.setViewportView(messagesPanel);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        var messageTextArea = new JTextArea();
        var btSendMessage = new JButton("Send");
        btSendMessage.addActionListener(evt -> {
            System.out.println("Sending \""+messageTextArea.getText()+"\"");
            // TODO GroupPanel takes a onMessageSent closure that'll actually create the message on the database and whatever else needs to be done
            // onMessageSent.accept(messageTextArea.getText());
            // and if we're gonna do that, we should also consider whether the "load newer messages" and "load previous messages"
            // should take a closure instead of explicitely using the msgRepo

            messageTextArea.setText("");
        });

        var newMessagePanel = new JPanel();
        newMessagePanel.setLayout(new BorderLayout());
        newMessagePanel.add(messageTextArea, BorderLayout.CENTER);
        newMessagePanel.add(btSendMessage, BorderLayout.LINE_END);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.PAGE_START);
        mainPanel.add(messagesScrollPane, BorderLayout.CENTER);
        mainPanel.add(newMessagePanel, BorderLayout.PAGE_END);
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
