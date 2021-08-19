package gui;

import gui.group.GroupListPanel;
import utils.AssetsUtil;

import javax.swing.*;

public class MainFrame extends JFrame
{
    public MainFrame(GroupListPanel groupsTab, JPanel friendsTab, MorePanel moreTab, JPanel settingsTab)
    {
        setTitle("Agree");
        this.setIconImage(AssetsUtil.getImage(AssetsUtil.ICON));
        var mainPane = new JTabbedPane();
        mainPane.addTab("Groups", groupsTab);
        mainPane.addTab("Friends", friendsTab);
        mainPane.addTab("More", moreTab);
        mainPane.addTab("Settings", settingsTab);
        setContentPane(mainPane);
    }

    public void display()
    {
        pack();
        setVisible(true);
    }
}
