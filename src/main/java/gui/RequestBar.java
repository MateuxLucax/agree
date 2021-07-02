package gui;

import models.User;
import models.request.Request;
import models.request.RequestState;

import javax.swing.*;
import java.awt.*;

public class RequestBar extends JPanel {
    private Request request;
    private User    viewer;

    private JPanel  buttonsPanel;
    private JButton btState;
    private JButton btAccept;
    private JButton btDecline;

    public RequestBar(Request request, User viewer) {
        this.request = request;
        this.viewer  = viewer;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        var icon = new JLabel(request.getIcon());  // Placeholder, we'll have an actual picture later
        add(icon, BorderLayout.LINE_START);

        var text = new JLabel(request.getText());
        add(text, BorderLayout.CENTER);

        var stateContainer = new JPanel();  // So the button doesn't occupy the whole LINE_END segment
        btState = new JButton(request.getState().toString()); // TODO add colors (accepted green, declined red)
        btState.setEnabled(false);
        stateContainer.add(btState);
        add(stateContainer, BorderLayout.LINE_END);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        add(buttonsPanel, BorderLayout.PAGE_END);

        btAccept = null;
        btDecline = null;
        if (request.getState() == RequestState.PENDING && request.to().equals(viewer)) {
            btAccept = new JButton("Accept");   // TODO add colors (accept green, decline red)
            btDecline = new JButton("Decline");
            buttonsPanel.add(btAccept);
            buttonsPanel.add(btDecline);
        }
    }

    // Update the request when the user accepts or declines it
    public void update() {
        btState.setText(request.getState().toString());
        buttonsPanel.removeAll();
        remove(buttonsPanel);
    }


    public void onAccept(Runnable callback) {
        if (btAccept == null) {
            throw new UnsupportedOperationException(
                "Can't add onAccept listener to request ("+request+"): not pending or not being viewer by the receiver"
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
                "Can't add onDecline listener to request ("+request+"): not pending or not being viewer by the receiver"
            );
        }
        btDecline.addActionListener(evt -> {
            callback.run();
            update();
        });
    }

}
