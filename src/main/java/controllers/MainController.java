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
        var groupsCon  = new GroupListController(user);
        var friendsCon = new FriendListController(user);
        var moreCon    = new MoreController(user);
        view = new MainFrame(groupsCon.getPanel(), friendsCon.getPanel(), moreCon.getPanel());
        view.display();
    }

    public void display()
    {
        view.display();
    }
}
