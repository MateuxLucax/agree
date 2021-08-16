package gui;

import models.invite.Invite;

import javax.swing.*;
import java.awt.*;

public class InviteBar extends JPanel
{
    private final JPanel buttonsPanel;
    private JButton btAccept;
    private JButton btDecline;
    private JButton btCancel;

    public InviteBar(Invite invite)
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // TODO show invite icon here
        // icon = (...) invite.getIcon() (...);
        // add(icon, BorderLayout.LINE_START);

        var text = new JLabel(invite.getText());
        add(text, BorderLayout.CENTER);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        add(buttonsPanel, BorderLayout.PAGE_END);
    }

    public void addAcceptAndDeclineButtons(Runnable onClickAccept, Runnable onClickDecline)
    {
        btAccept = new JButton("Accept");
        buttonsPanel.add(btAccept);
        btAccept.addActionListener(e -> onClickAccept.run());

        btDecline = new JButton("Decline");
        buttonsPanel.add(btDecline);
        btDecline.addActionListener(e -> onClickDecline.run());
    }

    public void warnCouldNotAcceptInvite()
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not accept the invite",
                "Accept",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void warnCouldNotDeclineInvite()
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not decline the invite",
                "Accept",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void addCancelButton(Runnable action)
    {
        btCancel = new JButton("Cancel");
        buttonsPanel.add(btCancel);
        btCancel.addActionListener(e -> action.run());
    }

    public boolean confirmCancel(Invite inv)
    {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to cancel the " + inv.kind() + "invite to " + inv.to().getNickname() + "?",
                "Cancel",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public void warnCouldNotCancelInvite()
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not cancel the invite",
                "Cancel",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
