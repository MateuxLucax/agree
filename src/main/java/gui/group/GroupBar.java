package gui.group;

import javax.swing.*;

public class GroupBar extends JPanel
{
    private final JLabel lbName;
    private final JButton btChat;
    private final JButton btMembers;
    private final JButton btInviteFriends;
    private final JButton btManage;

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
        btChat.addActionListener(e -> action.run());
    }

    public void onClickInvite(Runnable action)
    {
        btInviteFriends.addActionListener(e -> action.run());
    }

    public void onClickMembers(Runnable action)
    {
        btMembers.addActionListener(e -> action.run());
    }

    public void showManageButton()
    {
        add(btManage);
    }

    public void onClickManage(Runnable action)
    {
        btManage.addActionListener(e -> {
            btManage.setEnabled(false);
            action.run();
        });
    }

    public void removeManageButton() {
        remove(btManage);
    }
}
