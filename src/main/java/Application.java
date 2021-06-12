import data.UserDataAccess;
import gui.AuthPanel;
import gui.GroupBar;
import gui.GroupPanel;
import gui.UserBar;
import models.User;
import models.group.Group;
import services.login.ILoginService;
import services.login.LoginService;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Application {

    private static final UserDataAccess userDataAccess = UserDataAccess.getInstance();

    // Information about the user in session
    private User loggedUser;
    private List<Group> loggedUserGroups;
    private List<User> loggedUserFriends;

    private JFrame frame;
    private JPanel homePanel;

    public Application() {
        loggedUserGroups = new ArrayList<>();
        loggedUserFriends = new ArrayList<>();

        frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        homePanel = new JPanel();

        startSession();
    }

    private void startSession() {

        var authPanel = new AuthPanel();
        frame.add(authPanel.getJPanel());

        authPanel.setLoginListener(username -> {
            loggedUser = userDataAccess.retrieveUser();
            loggedUserFriends = userDataAccess.retrieveFriends(loggedUser);
            loggedUserGroups = userDataAccess.retrieveGroups(loggedUser);
            populateHomePanel();
            frame.remove(authPanel.getJPanel());
            frame.add(homePanel);
            frame.pack();
        });

        authPanel.setRegistrationListener((username, password) -> {
            userDataAccess.createAccount(username, password);
            loggedUser = userDataAccess.retrieveUser();
            loggedUserFriends = userDataAccess.retrieveFriends(loggedUser);
            loggedUserGroups = userDataAccess.retrieveGroups(loggedUser);
            populateHomePanel();
            frame.remove(authPanel.getJPanel());
            frame.add(homePanel);
            frame.pack();
        });

        frame.pack();
        frame.setVisible(true);
    }

    // Precondition: user is logged in,
    // loggedUserGroups and loggedUserFriends are initialized
    private void populateHomePanel() {
        var groupsPanel = new JPanel();
        var friendsPanel = new JPanel();

        for (var group : loggedUserGroups) {
            var groupPanel = new GroupPanel(group);
            groupPanel.setGoBackListener(evt -> {
                frame.remove(groupPanel.getJPanel());
                frame.add(homePanel);
                frame.pack();
                frame.repaint();
            });
            var groupBar = new GroupBar(group);
            groupBar.setOpenListener(evt -> {
                frame.remove(homePanel);
                frame.add(groupPanel.getJPanel());
                frame.pack();
                frame.repaint();
            });
            groupsPanel.add(groupBar.getJPanel());
        }
        for (var friend : loggedUserFriends)
            friendsPanel.add(new UserBar(friend).getJPanel());

        homePanel.add(groupsPanel);
        homePanel.add(friendsPanel);
    }

    public static void main(String[] args) {
        var app = new Application();
    }
}
