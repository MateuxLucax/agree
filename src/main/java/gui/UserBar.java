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

    public void showAskToBeFriendsButton()
    {
        btAskToBeFriends = new JButton("Ask to be friends");
        buttonsPanel.add(btAskToBeFriends);
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

    public void replaceWithInviteSentButton()
    {
        buttonsPanel.remove(btAskToBeFriends);
        showInviteSent();
        repaint();
        revalidate();
    }

    public void onClickAskToBeFriends(Runnable action)
    {
        if (btAskToBeFriends != null)
            btAskToBeFriends.addActionListener(e -> action.run());
    }
}
