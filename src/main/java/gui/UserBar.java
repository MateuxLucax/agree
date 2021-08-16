package gui;

import javax.swing.*;
import java.awt.*;

public class UserBar extends JPanel
{
    protected final JPanel buttonsPanel;
    private JButton btAskToBeFriends;

    public UserBar(String username)
    {
        setLayout(new BorderLayout());
        add(new JLabel(username), BorderLayout.PAGE_START);

        // TODO add profile picture icon on LINE_START

        buttonsPanel = new JPanel();
        add(buttonsPanel, BorderLayout.CENTER);
    }

    public void showAlreadyFriends()
    {
        var btAlreadyFriends = new JButton("Already friends");
        btAlreadyFriends.setEnabled(false);
        buttonsPanel.add(btAlreadyFriends);
    }

    public void showInviteSent()
    {
        var btInviteSent = new JButton("Friend invite sent");
        btInviteSent.setEnabled(false);
        buttonsPanel.add(btInviteSent);
    }

    public void updateInviteSituation()
    {
        if (btAskToBeFriends == null)
            return;
        buttonsPanel.remove(btAskToBeFriends);
        showInviteSent();
        repaint();
        revalidate();
    }

    public void addAskToBeFriendsButton(Runnable action)
    {
        btAskToBeFriends = new JButton("Ask to be friends");
        buttonsPanel.add(btAskToBeFriends);
        btAskToBeFriends.addActionListener(e -> action.run());
    }

    public void warnCouldNotSendInvite()
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not send the friend invite",
                "Ask to be friends",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
