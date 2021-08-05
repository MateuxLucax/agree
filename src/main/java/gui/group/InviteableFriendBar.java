package gui.group;

import javax.swing.*;
import java.awt.*;

// Does not extend UserBar
// because we don't need the friendship button functionality,
// since this bar is only created for users who already are friends.

public class InviteableFriendBar extends JPanel
{
    private JPanel buttonsPanel;
    private JButton btInviteToGroup;

    public InviteableFriendBar(String username) {
        setLayout(new BorderLayout());
        add(new JLabel(username), BorderLayout.PAGE_START);

        // TODO add profile picture icon on LINE_START

        buttonsPanel = new JPanel();
        add(buttonsPanel, BorderLayout.CENTER);
    }

    public void showInviteToGroupButton() {
        btInviteToGroup = new JButton("Invite to group");
        buttonsPanel.add(btInviteToGroup);
    }

    public void onClickInviteToGroup(Runnable action) {
        if (btInviteToGroup != null)
            btInviteToGroup.addActionListener(e -> action.run());
    }

    public void showGroupInviteSentButton() {
        var btInviteSent = new JButton("Group invite sent");
        btInviteSent.setEnabled(false);
        buttonsPanel.add(btInviteSent);
    }

    public void replaceWithGroupInviteSentButton() {
        buttonsPanel.remove(btInviteToGroup);
        showGroupInviteSentButton();
        repaint();
        revalidate();
    }
}
