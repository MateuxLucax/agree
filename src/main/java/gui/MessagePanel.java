package gui;

import models.message.Message;
import utils.DateUtil;
import utils.ImageUtil;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JPanel {

    private JButton btDelete;
    private final GridBagConstraints gridBagConstraints = new GridBagConstraints();

    public MessagePanel(Message msg) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        gridBagConstraints.insets = new Insets(8, 8, 8, 8);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        var lbPicture = new JLabel(ImageUtil.getImageIcon64(msg.getUser().getPicture()));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(lbPicture, gridBagConstraints);

        var lbUser   = new JLabel(msg.getUser().getNickname());
        lbUser.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 7));
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(lbUser,    gridBagConstraints);

        var lbText   = new JLabel(msg.getText());
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(lbText,    gridBagConstraints);

        var lbSentAt = new JLabel(DateUtil.dateToString(msg.sentAt()));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(lbSentAt,  gridBagConstraints);
    }

    public void showDeleteButton() {
        btDelete = new JButton("Delete");
        var container = new JPanel();  // Just so the button doesn't fill the whole segment
        container.add(btDelete);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        add(container, gridBagConstraints);
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