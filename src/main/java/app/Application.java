package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import gui.AuthPanel;
import gui.CreateGroupPanel;
import gui.GroupPanel;
import models.group.Group;
import utils.AssetsUtil;

import javax.swing.*;

public class Application {

    private UserSession userSession;

    private final JFrame frame;

    public Application() {
        // Initialize theme
        LafManager.install();
        LafManager.setTheme(new DarculaTheme());
        try {
            UIManager.setLookAndFeel(new DarculaThemeDarklafLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        frame = new JFrame("Agree");
        frame.setIconImage(AssetsUtil.getImage(AssetsUtil.ICON));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        startSession();
    }

    private void startSession() {
        var authPanel = new AuthPanel();
        frame.add(authPanel.getJPanel());

        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);

        // This stuff is what later will be in the controller for the AuthPanel, right?
        authPanel.setLoginListener((name, password) -> {
            if (name.isEmpty() || password.isEmpty()) {
                authPanel.warn("Username and password are required!");
                return;
            }
            try {
                userSession = UserSession.authenticate(name, password);
                frame.remove(authPanel.getJPanel());
                initializeFrame();
                frame.revalidate();
            } catch (UnauthorizedUserException e) {
                authPanel.warn("Incorrect username or password!");
            }
        });

        authPanel.setRegistrationListener((name, password) -> {
            if (name.isEmpty() || password.isEmpty()) {
                authPanel.warn("Username and password are required!");
                return;
            }
            try {
                userSession = UserSession.createAccount(name, password);
                frame.remove(authPanel.getJPanel());
                initializeFrame();
                frame.revalidate();
            } catch (NameAlreadyInUseException e) {
                authPanel.warn("Someone already uses the name " + name);
            } catch (UnsafePasswordException e) {
                authPanel.warn("Unsafe password!");
            }
        });
    }

    private void initializeFrame() {
        // For now the tabs only have text, but we can add icons to them
        // when we implement groups and users having their profile pictures:
        // https://stackoverflow.com/q/17648780
        var groupListPanel = new JTabbedPane(JTabbedPane.LEFT);
        for (var group : userSession.getGroups()) {
            boolean isOwner = userSession.getUser().equals(group.getOwner());
            groupListPanel.addTab(group.getName(), new GroupPanel(group, isOwner).getJPanel());
        }

        var createGroupPanel = new CreateGroupPanel();
        groupListPanel.addTab("+ New group", createGroupPanel.getJPanel());
        createGroupPanel.setCreationListener(groupName -> {
            var group = new Group(groupName, userSession.getUser());
            group.addUser(userSession.getUser());

            userSession.getGroupRepository().createGroup(group);

            // insert the new tab before the "+ New group" one, so it's always last
            groupListPanel.insertTab(
                    group.getName(),
                    null,
                    new GroupPanel(group, true).getJPanel(),
                    null,
                    groupListPanel.getTabCount()-1
            );
        });

        var friendListPanel = new JTabbedPane(JTabbedPane.LEFT);
        for (var friend : userSession.getFriends()) {
            var friendPanel = new JPanel();
            // not sure what to put here yet
            // if having someone as a friend automatically creates a group with just you and the friend only
            // then here we'll show a GroupPanel with said group
            friendPanel.add(new JLabel(friend.getNickname()));
            friendListPanel.addTab(friend.getNickname(), new JPanel());  // no UserPanel yet...
        }

        var sidePanel = new JTabbedPane(JTabbedPane.TOP);
        sidePanel.addTab("Groups", groupListPanel);
        sidePanel.addTab("Friends", friendListPanel);

        // var btCreateGroup = new JButton("Create group");

        frame.add(sidePanel);
    }

    public static void main(String[] args) {
        var app = new Application();
    }
}
