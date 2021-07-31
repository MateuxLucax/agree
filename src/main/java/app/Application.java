package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import controllers.AuthController;
import controllers.UserSearchController;
import controllers.group.GroupListController;
import gui.AuthPanel;
import gui.InviteBar;
import gui.InviteListFrame;
import gui.PopUpFrame;
import models.UserToGroupsMap;
import models.group.Group;
import models.invite.FriendshipInvite;
import models.invite.Invite;
import models.invite.InviteState;

import javax.swing.*;
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
        var authController = new AuthController();
        authController.display();
        authController.onSuccess(user -> {
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
            // FIXME if you click on "ask to be friends" for some user
            //   the button will turn into "invite sent", but if you
            //   click "search" again, the button will become "ask to be friends" again
            //   (this probably has to do with invites not being persisted correctly...)
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
                var invitesFrame = new InviteListFrame();
                invitesFrame.onClose(() -> btInvites.setEnabled(true));
                List<Invite> invites = session.getInviteRepository().getInvites(session.getUser());
                for (var inv : invites) {
                    var invBar = new InviteBar(inv, session.getUser());
                    invitesFrame.addInvite(invBar);
                    if (inv.getState() == InviteState.PENDING && inv.to().equals(session.getUser())) {
                        invBar.onAccept(() -> {
                            inv.setState(InviteState.ACCEPTED);
                            // TODO actually update invite in the database
                            if (inv instanceof FriendshipInvite) {
                                // TODO actually add friendship relationship in the database
                            } else {  // inv instanceof GroupInvite
                                // TODO actually add user to the group in the database
                            }
                        });
                        invBar.onDecline(() -> {
                            inv.setState(InviteState.DECLINED);
                            // TODO actually update invite in the database
                        });
                    }
                }
                invitesFrame.pack();
                invitesFrame.setVisible(true);
            });

            // usg: [U]sers in the [S]ame [G]roup as you
            // (couldn't think of a more concise name, so I just made up an acronym to avoid really long variable names)
            // TODO encapsulate the GUI stuff into a UserInSameGroupFrame
            var btUsg = new JButton("Users in the same groups as you");
            morePanel.add(btUsg);
            btUsg.addActionListener(evt -> {
                btUsg.setEnabled(false);
                var usgFrame = new PopUpFrame();
                usgFrame.onClose(() -> btUsg.setEnabled(true));
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
