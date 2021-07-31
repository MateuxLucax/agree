package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import controllers.AuthController;
import controllers.InviteListController;
import controllers.UserSearchController;
import controllers.group.GroupListController;
import gui.InviteBar;
import gui.InviteListFrame;
import models.UserToGroupsMap;
import models.group.Group;
import models.invite.FriendshipInvite;
import models.invite.Invite;
import models.invite.InviteState;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class Application {

    private UserSession session;

    public Application() {
        initializeTheme();
        startUserSession();
    }

    private void initializeTheme() {
        LafManager.install();
        LafManager.setTheme(new DarculaTheme());
        try {
            UIManager.setLookAndFeel(new DarculaThemeDarklafLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void startUserSession()
    {
        session = UserSession.getInstance(); // Shortcut
        var authCon = new AuthController();
        authCon.display();
        authCon.onSuccess(user -> {
            session.initialize(user);
            initializeMainFrame();
        });
    }

    private void initializeMainFrame() {
        var mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        var mainPane = new JTabbedPane();

        var groupListCon = new GroupListController(session.getUser());
        mainPane.addTab("Groups", groupListCon.getPanel());

        var friendsPanel = new JPanel();
        mainPane.addTab("Friends", friendsPanel);
        // TODO add "friends" tab to the application

        var morePanel = new JPanel();
        mainPane.addTab("More", morePanel);
        {
            var btSearchForUsers = new JButton("Search for users");
            morePanel.add(btSearchForUsers);
            btSearchForUsers.addActionListener(evt -> {
                btSearchForUsers.setEnabled(false);
                var searchCon = new UserSearchController(session.getUser());
                searchCon.onClose(() -> btSearchForUsers.setEnabled(true));
                searchCon.display();
            });

            var btInvites = new JButton("Invites");
            morePanel.add(btInvites);
            btInvites.addActionListener(evt -> {
                btInvites.setEnabled(false);
                var invitesCon = new InviteListController(session.getUser());
                invitesCon.onClose(() -> btInvites.setEnabled(true));
                invitesCon.display();
            });

            // usg: [U]sers in the [S]ame [G]roup as you
            // (couldn't think of a more concise name, so I just made up an acronym to avoid really long variable names)
            // TODO encapsulate the GUI stuff into a UserInSameGroupFrame
            var btUsg = new JButton("Users in the same groups as you");
            morePanel.add(btUsg);
            btUsg.addActionListener(evt -> {
                btUsg.setEnabled(false);
                var usgFrame = new JFrame();
                usgFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        btUsg.setEnabled(true);
                        usgFrame.dispose();
                    }
                });
                var usgScrollPane = new JScrollPane();
                usgScrollPane.getVerticalScrollBar().setUnitIncrement(20);
                var usgPanel = new JPanel();
                usgScrollPane.setViewportView(usgPanel);
                usgPanel.setLayout(new BoxLayout(usgPanel, BoxLayout.PAGE_AXIS));
                var usgMap = new UserToGroupsMap();
                for (var group : session.getGroups()) {
                    // Don't show the user in session himself in this list
                    if (group.isMember(session.getUser()))
                        continue;
                    usgMap.add(group.getOwner(), group);
                    for (var user : group.getUsers())
                        usgMap.add(user, group);
                }
                for (var user : usgMap.userSet()) {
                    List<Group> groups = usgMap.get(user);
                    // Show which groups the user has in common in a string formatted like
                    // user123 belongs to group1, The Group and final group
                    String strGroups = user.getNickname() + " belongs to " + groups;
                    usgPanel.add(new JLabel(strGroups));
                    // Just in a JLabel for now
                }
                usgFrame.setContentPane(usgScrollPane);
                usgFrame.pack();
                usgFrame.setVisible(true);
            });
        }
        mainFrame.setContentPane(mainPane);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new Application();
    }
}
