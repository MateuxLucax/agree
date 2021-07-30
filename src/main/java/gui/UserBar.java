package gui;

import javax.swing.*;
import java.awt.*;

public class UserBar extends JPanel
{
    private final JPanel buttonPanel;
    private JButton btAskToBeFriends;

    public UserBar(String username)
    {
        setLayout(new BorderLayout());
        add(new JLabel(username), BorderLayout.PAGE_START);

        // TODO add profile picture icon on LINE_START

        buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.CENTER);
    }

    public void showAskToBeFriendsButton()
    {
        btAskToBeFriends = new JButton("Ask to be friends");
        buttonPanel.add(btAskToBeFriends);
    }

    public void showAlreadyFriendsButton()
    {
        var btAlreadyFriends = new JButton("Already friends");
        btAlreadyFriends.setEnabled(false);
        buttonPanel.add(btAlreadyFriends);
    }

    public void showInviteSentButton()
    {
        var btInviteSent = new JButton("Invite sent");
        btInviteSent.setEnabled(false);
        buttonPanel.add(btInviteSent);
    }

    public void replaceWithInviteSentButton()
    {
        buttonPanel.remove(btAskToBeFriends);
        showInviteSentButton();
        repaint();
        revalidate();
    }

    public void onClickAskToBeFriends(Runnable action)
    {
        if (btAskToBeFriends != null)
            btAskToBeFriends.addActionListener(e -> action.run());
    }
}
