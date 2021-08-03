package gui;

import gui.group.GroupListPanel;

import javax.swing.*;

public class MainFrame extends JFrame
{
    public MainFrame(GroupListPanel groupsTab, JPanel friendsTab, MorePanel moreTab)
    {
        var mainPane = new JTabbedPane();
        mainPane.addTab("Groups", groupsTab);
        mainPane.addTab("Friends", friendsTab);
        mainPane.addTab("More", moreTab);
        setContentPane(mainPane);
    }

    public void display()
    {
        pack();
        setVisible(true);
    }
}
