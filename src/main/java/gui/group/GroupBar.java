package gui.group;

import javax.swing.*;

public class GroupBar extends JPanel
{
    private JLabel lbName;
    private JButton btChat;
    private JButton btMembers;
    private JButton btInviteFriends;
    private JButton btManage;

    public GroupBar(String groupName)
    {
        lbName = new JLabel(groupName);
        add(lbName);

        btChat = new JButton("Chat");
        add(btChat);

        btMembers = new JButton("Members");
        add(btMembers);

        btInviteFriends = new JButton("Invite friends");
        add(btInviteFriends);

        btManage = new JButton("Manage");
    }

    public JButton getChatButton()    { return btChat; }
    public JButton getMembersButton() { return btMembers; }
    public JButton getInviteButton()  { return btInviteFriends; }
    public JButton getManageButton()  { return btManage; }

    public void rename(String newName)
    {
        lbName.setText(newName);
    }

    // TODO consider if the .setEnabled(false) stuff should really be here
    //   and not on the controller

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

    public void onClickMembers(Runnable action)
    {
        btMembers.addActionListener(e -> {
            btMembers.setEnabled(false);
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
