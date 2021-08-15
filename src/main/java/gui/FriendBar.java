package gui;

import javax.swing.*;

public class FriendBar extends JPanel
{
    private final JButton btChat;
    private final JButton btUnfriend;
    private final String friendName;

    public FriendBar(String friendName)
    {
        this.friendName = friendName;

        var lbName = new JLabel(friendName);
        add(lbName);

        btChat = new JButton("Chat");
        add(btChat);

        btUnfriend = new JButton("Unfriend");
        add(btUnfriend);
    }

    public void onClickChat(Runnable action)
    {
        btChat.addActionListener(e -> action.run());
    }

    public void chatButtonSetEnabled(boolean b)
    {
        btChat.setEnabled(b);
    }

    public void onClickUnfriend(Runnable action)
    {
        btUnfriend.addActionListener(e -> action.run());
    }

    public boolean confirmUnfriend()
    {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to unfriend " + friendName + "?",
                "Unfriend",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public void warnCouldNotUnfriend()
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not unfriend " + friendName,
                "Unfriend",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
