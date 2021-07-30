package gui;

import models.User;
import models.invite.Invite;
import models.invite.InviteState;

import javax.swing.*;
import java.awt.*;

public class InviteBar extends JPanel {
    private final Invite invite;

    private final JPanel  buttonsPanel;
    private final JButton btState;
    private JButton btAccept;
    private JButton btDecline;

    public InviteBar(Invite invite, User viewer) {
        this.invite = invite;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        var icon = new JLabel(invite.getIcon());  // Placeholder, we'll have an actual picture later
        add(icon, BorderLayout.LINE_START);

        var text = new JLabel(invite.getText());
        add(text, BorderLayout.CENTER);

        var stateContainer = new JPanel();  // So the button doesn't occupy the whole LINE_END segment
        btState = new JButton(invite.getState().toString()); // TODO add colors (accepted green, declined red)
        btState.setEnabled(false);
        stateContainer.add(btState);
        add(stateContainer, BorderLayout.LINE_END);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        add(buttonsPanel, BorderLayout.PAGE_END);

        btAccept = null;
        btDecline = null;
        if (invite.getState() == InviteState.PENDING && invite.to().equals(viewer)) {
            btAccept = new JButton("Accept");   // TODO add colors (accept green, decline red)
            btDecline = new JButton("Decline");
            buttonsPanel.add(btAccept);
            buttonsPanel.add(btDecline);
        }
    }

    // Update the request when the user accepts or declines it
    public void update() {
        btState.setText(invite.getState().toString());
        buttonsPanel.removeAll();
        remove(buttonsPanel);
    }


    public void onAccept(Runnable callback) {
        if (btAccept == null) {
            throw new UnsupportedOperationException(
                "Can't add onAccept listener to request ("+ invite +"): not pending or not being viewer by the receiver"
            );
        }
        btAccept.addActionListener(evt -> {
            callback.run();
            update();
        });
    }

    public void onDecline(Runnable callback) {
        if (btDecline == null) {
            throw new UnsupportedOperationException(
                "Can't add onDecline listener to request ("+ invite +"): not pending or not being viewer by the receiver"
            );
        }
        btDecline.addActionListener(evt -> {
            callback.run();
            update();
        });
    }

}
