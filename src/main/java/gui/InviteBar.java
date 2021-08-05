package gui;

import models.invite.Invite;

import javax.swing.*;
import java.awt.*;

public class InviteBar extends JPanel
{
    private JPanel buttonsPanel;
    private JButton btAccept;
    private JButton btDecline;

    public InviteBar(Invite invite)
    {

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        // TODO show invite icon here
        // icon = (...) invite.getIcon() (...);
        // add(icon, BorderLayout.LINE_START);

        var text = new JLabel(invite.getText());
        add(text, BorderLayout.CENTER);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        add(buttonsPanel, BorderLayout.PAGE_END);
    }

    public void showAcceptAndDeclineButtons()
    {
        btAccept = new JButton("Accept");
        btDecline = new JButton("Decline");
        buttonsPanel.add(btAccept);
        buttonsPanel.add(btDecline);

    }

    public void onClickAccept(Runnable action)
    {
        if (btAccept != null)
            btAccept.addActionListener(e -> action.run());
    }

    public void onClickDecline(Runnable action)
    {
        if (btDecline != null)
            btDecline.addActionListener(e -> action.run());
    }
}
