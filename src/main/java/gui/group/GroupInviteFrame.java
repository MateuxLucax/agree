package gui.group;

import gui.PopUpFrame;
import gui.UserBar;

import javax.swing.*;

public class GroupInviteFrame extends PopUpFrame
{
    private final JPanel friendsPanel;

    public GroupInviteFrame(JButton btnThatOpenedTheFrame)
    {
        super(btnThatOpenedTheFrame);

        var friendsScrollPane = new JScrollPane();
        friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.PAGE_AXIS));

        friendsScrollPane.setViewportView(friendsPanel);
        friendsScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(friendsScrollPane);
    }

    public void addFriendBar(UserBar bar)
    {
        friendsPanel.add(bar);
    }
}
