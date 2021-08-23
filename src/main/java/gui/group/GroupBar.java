package gui.group;

import models.group.Group;
import utils.ImageUtil;

import javax.swing.*;

public class GroupBar extends JPanel
{
    private final JLabel lbName;

    private final JButton btChat;
    private final JButton btMembers;
    private final JButton btInviteFriends;
    private final JButton btManage;
    private final JButton btQuit;
    private final String groupName;

    public GroupBar(Group group)
    {
        this.groupName = group.getName();

        var lbPicture = new JLabel(ImageUtil.getImageIcon64(group.getPicture()));
        add(lbPicture);

        lbName = new JLabel(groupName);
        add(lbName);

        btChat = new JButton("Chat");
        add(btChat);

        btMembers = new JButton("Members");
        add(btMembers);

        btInviteFriends = new JButton("Invite friends");
        add(btInviteFriends);

        btManage = new JButton("Manage");

        btQuit = new JButton("Quit");
    }

    // TODO these get methods are just called to call setEnabled on the button,
    //   so instead do chatButtonSetEnabled(...) methods
    public JButton getChatButton()    { return btChat; }
    public JButton getMembersButton() { return btMembers; }
    public JButton getInviteButton()  { return btInviteFriends; }
    public JButton getManageButton()  { return btManage; }

    public void updateGroup(Group group) {
        lbName.setText(group.getName());
        repaint();
        revalidate();
    }

    public void onClickChat(Runnable action) {
        btChat.addActionListener(e -> action.run());
    }

    public void onClickInvite(Runnable action) {
        btInviteFriends.addActionListener(e -> action.run());
    }

    public void onClickMembers(Runnable action) {
        btMembers.addActionListener(e -> action.run());
    }

    public void addManageButton(Runnable action) {
        add(btManage);
        btManage.addActionListener(e -> action.run());
    }

    public void addQuitButton(Runnable action) {
        add(btQuit);
        btQuit.addActionListener(e -> action.run());
    }

    public void replaceManageWithQuitButton(Runnable quitAction) {
        remove(btManage);
        add(btQuit);
        btQuit.addActionListener(e -> quitAction.run());
        repaint();
        revalidate();
    }

    public boolean confirmQuit() {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to quit the group " + groupName + "?",
                "Quit",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public void warnCouldNotQuit() {
        JOptionPane.showMessageDialog(
                this,
                "Could not quit the group " + groupName,
                "Quit",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
