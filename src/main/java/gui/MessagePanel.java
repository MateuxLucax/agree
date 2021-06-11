package gui;

import models.message.Message;

import javax.swing.*;

// TODO show profile picture of author

public class MessagePanel {
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JLabel lbText;
    private JLabel lbAuthor;
    private JLabel lbSentAt;

    public MessagePanel(Message msg) {
        lbAuthor.setText(msg.getUser().getNickname());
        lbSentAt.setText(msg.getSentAt().toString());
        lbText.setText(msg.getText());
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
    // TODO make all these EtcPanel classes extend JPanel so that this getPanel method is unnecessary
}
