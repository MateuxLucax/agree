package gui;

// Where the user actually reads and writes messages

import app.UserSession;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageInFileRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;

public class GroupPanel {

    // private final User user = UserSession.getInstance().getUser();
    // private final IMessageRepository messageRepository = new MessageInFileRepository();
    // unused?

    private final JPanel mainPanel;

    private final Group group;

    private final JPanel messageListPanel;
    private final JButton btLoadOlder;
    private final JButton btLoadNewer;

    private final JTextArea taMessageText;
    private final JButton btSendMessage;

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
        taMessageText.setBorder(new EmptyBorder(0, 0, 0, 16));
        btSendMessage = new JButton("Send");

        var newMessagePanel = new JPanel();
        newMessagePanel.setLayout(new BorderLayout());
        newMessagePanel.setBorder(new EmptyBorder(16, 16, 16, 16));
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
