package gui;

import models.User;
import models.invite.Invite;
import models.invite.InviteState;

import javax.swing.*;
import java.awt.*;

public class InviteBar extends JPanel
{
    private final Invite invite;
    private final JPanel  buttonsPanel;
    private final JButton btState;
    private JButton btAccept;
    private JButton btDecline;

    public InviteBar(Invite invite)
    {
        this.invite = invite;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        // TODO show invite icon here
        // icon = (...) invite.getIcon() (...);
        // add(icon, BorderLayout.LINE_START);

        var text = new JLabel(invite.getText());
        add(text, BorderLayout.CENTER);

        // We put the button inside a JPanel (btnContainer) and the JPanel in the InviteBar
        // because if we put the button directly into the InviteBar it'd fill the whole LINE_END
        // part of the layout
        var btnContainer = new JPanel();
        // TODO add colors (accepted green, declined red)
        btState = new JButton(invite.getState().toString());
        btState.setEnabled(false);
        btnContainer.add(btState);
        add(btnContainer, BorderLayout.LINE_END);

        // Container for the "accept" and "decline" buttons
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        add(buttonsPanel, BorderLayout.PAGE_END);
    }

    public void showAcceptAndDeclineButtons()
    {
        // TODO add colors (accept green, decline red)
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

    public void removeAcceptAndDeclineButtons()
    {
        buttonsPanel.removeAll();
        remove(buttonsPanel);
    }

    public void updateStateButton()
    {
        btState.setText(invite.getState().toString());
    }
}
