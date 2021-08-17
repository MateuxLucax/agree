package gui;

import models.message.Message;

import javax.swing.*;
import java.awt.*;

// TODO show profile picture of author

public class MessagePanel extends JPanel {

    private JButton btDelete;

    public MessagePanel(Message msg) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        var lbUser   = new JLabel(msg.getUser().getNickname());
        lbUser.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 7));
        var lbText   = new JLabel(msg.getText());
        var lbSentAt = new JLabel(msg.sentAt().toString());  // TODO nicer looking date

        add(lbUser,   BorderLayout.LINE_START);
        add(lbText,   BorderLayout.CENTER);
        add(lbSentAt, BorderLayout.PAGE_START);
    }

    public void showDeleteButton() {
        btDelete = new JButton("Delete");
        var container = new JPanel();  // Just so the button doesn't fill the whole segment
        container.add(btDelete);
        add(container, BorderLayout.LINE_END);
    }

    public void onClickDelete(Runnable action) {
        assert btDelete != null : "onClickDelete has been called but showDeleteButton hasn't";
        btDelete.addActionListener(e -> action.run());
    }

    public boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to delete this message?",
                "Delete",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public void warnCouldNotDelete() {
        JOptionPane.showMessageDialog(
                this,
                "Could not delete the message",
                "Delete",
                JOptionPane.ERROR_MESSAGE
        );
    }
}