package gui.group;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GroupInviteFrame extends JFrame
{
    private final JPanel friendsPanel;
    private Runnable onClose;

    public GroupInviteFrame(String groupName)
    {
        setTitle(groupName + ": invite friends");

        var friendsScrollPane = new JScrollPane();
        friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.PAGE_AXIS));

        friendsScrollPane.setViewportView(friendsPanel);
        friendsScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(friendsScrollPane);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (onClose != null)
                    onClose.run();
                dispose();
            }
        });
    }

    public void onClose(Runnable action)
    {
        onClose = action;
    }

    public void addFriendBar(InviteableFriendBar bar)
    {
        friendsPanel.add(bar);
    }
}
