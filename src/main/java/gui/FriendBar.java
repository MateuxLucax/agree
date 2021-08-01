package gui;

import javax.swing.*;

public class FriendBar extends JPanel
{
    private final JButton btChat;
    private final JButton btUnfriend;

    public FriendBar(String friendName)
    {
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
}
