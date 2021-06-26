package gui;

// Where the user actually reads and writes messages

import models.group.Group;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Function;

public class GroupPanel extends JTabbedPane {

    // private final User user = UserSession.getInstance().getUser();
    // private final IMessageRepository messageRepository = new MessageInFileRepository();
    // unused?

    private final Group group;

    private final JPanel messageListPanel;
    private final JButton btLoadOlder;
    private final JButton btLoadNewer;

    private final JTextArea taMessageText;
    private final JButton btSendMessage;

    private final GroupManagementPanel managPanel;

    public void setLoadOlderButtonListener(ActionListener listener) {
        btLoadOlder.addActionListener(listener);
    }

    public void setLoadNewerButtonListener(ActionListener listener) {
        btLoadNewer.addActionListener(listener);
    }

    public void setSendButtonListener(Function<String, Boolean> onNewMessage) {
        btSendMessage.addActionListener(evt -> {
            if (onNewMessage.apply(taMessageText.getText()))
                taMessageText.setText("");
        });
    }

    // We make the management panel itself available like this
    // so the Application can configure it -- set what happens
    // when the owner presses the "delete" button and so on
    public GroupManagementPanel getManagementPanel() {
        return managPanel;
    }

    public GroupPanel(Group group, boolean showingToOwner) {
        this.group = group;

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
        taMessageText.setBorder(new EmptyBorder(0, 0, 0, 16));
        btSendMessage = new JButton("Send");

        var newMessagePanel = new JPanel();
        newMessagePanel.setLayout(new BorderLayout());
        newMessagePanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        newMessagePanel.add(taMessageText, BorderLayout.CENTER);
        newMessagePanel.add(btSendMessage, BorderLayout.LINE_END);

        var messagesTab = new JPanel(new BorderLayout());
        messagesTab.add(messagesScrollPane, BorderLayout.CENTER);
        messagesTab.add(newMessagePanel, BorderLayout.PAGE_END);

        addTab("Messages", messagesTab);

        // TODO tab to list the group's members
        // which also takes a 'showingToOwner' option,
        // where if it's true it'll show a 'remove' button next to each user
        // so the owner can remove them

        if (showingToOwner) {
            managPanel = new GroupManagementPanel(group.getName());
            addTab("Manage", managPanel.getJPanel());
        } else {
            managPanel = null;
        }
    }

    public void refreshMessageListPanel() {
        messageListPanel.removeAll();
        for (var msg : group.getMessages())
            messageListPanel.add(new MessagePanel(msg).getJPanel());
        messageListPanel.revalidate();
    }
}
