package controllers;

import controllers.group.GroupListController;
import gui.MainFrame;
import models.User;

import javax.swing.*;

public class MainController
{
    private final MainFrame view;

    public MainController(User user)
    {
        var groupsCon = new GroupListController(user);
        // TODO figure out what to put on the friends tab
        var friendsPanel = new JPanel();
        var moreCon = new MoreController(user);
        view = new MainFrame(groupsCon.getPanel(), friendsPanel, moreCon.getPanel());
        view.display();
    }

    public void display()
    {
        view.display();
    }
}
