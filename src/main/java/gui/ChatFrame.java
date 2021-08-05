package gui;

import models.message.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.function.Function;

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

    public void loadMessages(LinkedList<Message> messages) {
        msgListPanel.removeAll();
        for (var msg : messages)
            msgListPanel.add(new MessagePanel(msg).getJPanel());
        msgListPanel.revalidate();
        msgListPanel.repaint();
    }

    public void onLoadOlder(Runnable onLoadOlder) {
        btLoadOlder.addActionListener(evt -> onLoadOlder.run());
    }

    public void onLoadNewer(Runnable onLoadNewer) {
        btLoadNewer.addActionListener(evt -> onLoadNewer.run());
    }

    public void onSendMessage(Function<String, Boolean> onSend) {
        btSend.addActionListener(evt -> {
            if (onSend.apply(taNewMsg.getText()))
                taNewMsg.setText("");
            // TODO else warn that the message could not be sent
        });
    }

    public void display()
    {
        pack();
        setVisible(true);
    }


}
