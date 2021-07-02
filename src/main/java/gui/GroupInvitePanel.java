package gui;

import javax.swing.*;

public class GroupInvitePanel extends JScrollPane {
    private JPanel friendsPanel;

    public GroupInvitePanel() {
        friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.PAGE_AXIS));

        setViewportView(friendsPanel);
        getVerticalScrollBar().setUnitIncrement(20);
    }

    public void addBar(UserBar bar) {
        friendsPanel.add(bar);
    }
}
