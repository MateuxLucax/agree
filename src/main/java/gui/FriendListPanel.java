package gui;

import javax.swing.*;

public class FriendListPanel extends JPanel
{
    private JPanel panel;

    public FriendListPanel()
    {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        var scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane);
    }

    public void addFriendBar(FriendBar bar)
    {
        panel.add(bar);
    }

    public void removeFriendBar(FriendBar bar)
    {
        panel.remove(bar);
    }
}
