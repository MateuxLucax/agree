package gui;

// Where the user actually reads and writes messages

import models.group.Group;
import models.message.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.function.Consumer;

public class GroupPanel {

    private JPanel mainPanel;

    private Group group;

    private JPanel messageListPanel;
    private JButton btLoadOlder;
    private JButton btLoadNewer;

    private JTextArea taMessageText;
    private JButton btSendMessage;

    public void setLoadOlderButtonListener(ActionListener listener) {
        btLoadOlder.addActionListener(listener);
    }

    public void setLoadNewerButtonListener(ActionListener listener) {
        btLoadNewer.addActionListener(listener);
    }

    public void setSendButtonListener(Consumer<String> onNewMessage) {
        btSendMessage.addActionListener(evt -> {
            onNewMessage.accept(taMessageText.getText());
            taMessageText.setText("");
        });
    }

    public GroupPanel(Group group, boolean showingToOwner) {
        this.group = group;

        /* mainPanel
         * \--- headerPanel (name[, manageGroup if owner])
         * \--- messagesScrollPane
         *      \--- messagesPanel
         *           \--- btLoadOlder
         *           \--- messageListPanel
         *           \--- btLoadNewer
         */

        var lbName = new JLabel(group.getName());
        var headerPanel = new JPanel();
        headerPanel.add(lbName);

        if (showingToOwner) {
            JButton btManage = new JButton("Manage");
            headerPanel.add(btManage);
            // TODO actual group management panel (delete group, renaming group, removing users etc.)
        }

        messageListPanel = new JPanel();
        messageListPanel.setLayout(new BoxLayout(messageListPanel, BoxLayout.PAGE_AXIS));
        refreshMessageListPanel();

        btLoadOlder = new JButton("Load older messages");
        btLoadNewer = new JButton("Load newer messages");

        var messagesPanel = new JPanel();
        messagesPanel.setLayout(new BorderLayout());
        messagesPanel.add(btLoadOlder, BorderLayout.PAGE_START);
        messagesPanel.add(messageListPanel, BorderLayout.CENTER);
        messagesPanel.add(btLoadNewer, BorderLayout.PAGE_END);

        var messagesScrollPane = new JScrollPane();
        messagesScrollPane.setViewportView(messagesPanel);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        taMessageText = new JTextArea();
        btSendMessage = new JButton("Send");

        var newMessagePanel = new JPanel();
        newMessagePanel.setLayout(new BorderLayout());
        newMessagePanel.add(taMessageText, BorderLayout.CENTER);
        newMessagePanel.add(btSendMessage, BorderLayout.LINE_END);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.PAGE_START);
        mainPanel.add(messagesScrollPane, BorderLayout.CENTER);
        mainPanel.add(newMessagePanel, BorderLayout.PAGE_END);
    }

    public void refreshMessageListPanel() {
        messageListPanel.removeAll();
        for (var msg : group.getMessages())
            messageListPanel.add(new MessagePanel(msg).getJPanel());
        messageListPanel.revalidate();
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
