package controllers;

import controllers.group.GroupListController;
import gui.MainFrame;
import models.User;
import repositories.DBConnection;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        // If the user clicks on the X button on the window
        view.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                DBConnection.disconnect();
                System.exit(0);
            }
        });
    }

    public void display()
    {
        view.display();
    }
}
