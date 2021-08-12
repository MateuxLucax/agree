package gui;

import models.message.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.Consumer;

public class ChatFrame extends JFrame
{
    private final JPanel msgListPanel;
    private final JButton btLoadOlder;
    private final JButton btLoadNewer;
    private final JTextArea taNewMsg;
    private final JButton btSend;
    private Runnable onClose;

    public ChatFrame(String title)
    {
        setTitle(title);

        msgListPanel = new JPanel();
        msgListPanel.setLayout(new BoxLayout(msgListPanel, BoxLayout.PAGE_AXIS));

        btLoadOlder = new JButton("Load older messages");
        btLoadNewer = new JButton("Load newer messages");

        var msgsPanel = new JPanel(new BorderLayout());
        msgsPanel.add(btLoadOlder, BorderLayout.PAGE_START);
        msgsPanel.add(msgListPanel, BorderLayout.CENTER);
        msgsPanel.add(btLoadNewer, BorderLayout.PAGE_END);

        var scrollPane = new JScrollPane();
        scrollPane.setViewportView(msgsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        taNewMsg = new JTextArea();
        taNewMsg.setBorder(new EmptyBorder(0, 0, 0, 16));
        btSend = new JButton("Send");

        var newMsgPanel = new JPanel();
        newMsgPanel.setLayout(new BorderLayout());
        newMsgPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        newMsgPanel.add(taNewMsg, BorderLayout.CENTER);
        newMsgPanel.add(btSend, BorderLayout.LINE_END);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(newMsgPanel, BorderLayout.PAGE_END);

        setContentPane(mainPanel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (onClose != null)
                    onClose.run();
                dispose();
            }
        });
    }

    public void onClose(Runnable action)
    {
        onClose = action;
    }

    public void addMessagesBelow(List<Message> messages) {
        for (var msg : messages)
            msgListPanel.add(new MessagePanel(msg).getJPanel());
        msgListPanel.revalidate();
        msgListPanel.repaint();
    }

    public void addMessagesAbove(List<Message> messages) {
        // For the messages to be shown in the order they came,
        // we need to push to the top in reverse order
        // [1, 2, 3]           how they'll be shown:
        //  ^  ^  ^-- push(3)  [3]
        //  |  '----- push(2)  [2, 3]
        //  '-------- push(1)  [1, 2, 3]
        for (int i = messages.size()-1; i >= 0; i--)
            msgListPanel.add(new MessagePanel(messages.get(i)).getJPanel(), 0);
        msgListPanel.revalidate();
        msgListPanel.repaint();
    }

    public void onLoadOlder(Runnable onLoadOlder) {
        btLoadOlder.addActionListener(evt -> onLoadOlder.run());
    }

    public void onLoadNewer(Runnable onLoadNewer) {
        btLoadNewer.addActionListener(evt -> onLoadNewer.run());
    }

    public void onSendMessage(Consumer<String> onSend) {
        btSend.addActionListener(evt -> onSend.accept(taNewMsg.getText()));
    }

    public void clearMessageTextarea() {
        taNewMsg.setText("");
    }

    public void display() {
        pack();
        setVisible(true);
    }


}
