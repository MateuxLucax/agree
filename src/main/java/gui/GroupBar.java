package gui;

import javax.swing.*;

public class GroupBar extends JPanel
{
    private JLabel lbName;
    private JButton btChat;
    private JButton btInviteFriends;
    private JButton btManage;

    public GroupBar(String groupName)
    {
        lbName = new JLabel(groupName);
        add(lbName);

        btChat = new JButton("Chat");
        add(btChat);

        btInviteFriends = new JButton("Invite friends");
        add(btInviteFriends);

        btManage = new JButton("Manage");
    }

    public JButton getChatButton()   { return btChat; }
    public JButton getInviteButton() { return btInviteFriends; }
    public JButton getManageButton() { return btManage; }

    public void rename(String newName)
    {
        lbName.setText(newName);
    }

    public void onClickChat(Runnable action)
    {
        btChat.addActionListener(e -> {
            btChat.setEnabled(false);
            action.run();
        });
    }

    public void onClickInvite(Runnable action)
    {
        btInviteFriends.addActionListener(e -> {
            btInviteFriends.setEnabled(false);
            action.run();
        });
    }

    public void showManageButton() { add(btManage); }

    public void onClickManage(Runnable action)
    {
        btManage.addActionListener(e -> {
            btManage.setEnabled(false);
            action.run();
        });
    }
}
