package gui;

import javax.swing.*;
import java.awt.*;

public class FriendListPanel extends JPanel
{
    private final JPanel friendsPanel;
    private final JButton btRefresh;

    public FriendListPanel() {
        setLayout(new BorderLayout());

        var btContainer = new JPanel();
        btRefresh = new JButton("Refresh");
        btContainer.add(btRefresh);
        add(btContainer, BorderLayout.PAGE_START);
        // Button is in container and not directly in the panel
        // because with the BorderLayout.PAGE_START it'd fill that
        // whole segment of the layout.

        friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.PAGE_AXIS));

        var scrollPane = new JScrollPane();
        scrollPane.setViewportView(friendsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addFriendBar(FriendBar bar) {
        friendsPanel.add(bar);
    }

    public void removeFriendBar(FriendBar bar) {
        friendsPanel.remove(bar);
        repaint();
        revalidate();
    }

    public void clear() {
        friendsPanel.removeAll();
    }

    public void onClickRefresh(Runnable action) {
        btRefresh.addActionListener(e -> action.run());
    }
}
