package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import controllers.*;
import gui.*;
import models.User;
import models.UserToGroupsMap;
import models.group.Group;
import models.invite.FriendshipInvite;
import models.invite.Invite;
import models.invite.InviteState;
import repositories.group.IGroupRepository;
import repositories.user.IUserRepository;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private UserSession session;
    private IGroupRepository groupRepo;
    private IUserRepository userRepo;

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

    private void startUserSession() {
        var authFrame = new JFrame();
        var authPanel = new AuthPanel();
        authFrame.setContentPane(authPanel.getJPanel());
        authFrame.pack();
        authFrame.setVisible(true);

        var authController = new AuthController(authFrame, authPanel);
        authController.onSuccess(user -> {
            session.initialize(user);
            initializeMainFrame();
        });

        // Setting up some shortcuts
        session   = UserSession.getInstance();
        groupRepo = session.getGroupRepository();
        userRepo  = session.getUserRepository();
    }

    private void initializeMainFrame() {
        var mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        var mainPane = new JTabbedPane();
        var groupsPanel = new JPanel();
        mainPane.addTab("Groups", groupsPanel);
        {
            groupsPanel.setLayout(new BoxLayout(groupsPanel, BoxLayout.PAGE_AXIS));
            var btNewGroup = new JButton("+ New group");
            groupsPanel.add(btNewGroup);
            btNewGroup.addActionListener(evt -> {
                btNewGroup.setEnabled(false);
                var groupCreationFrame = new GroupCreationFrame(btNewGroup);
                groupCreationFrame.onCreation(groupName -> {
                    var group = new Group(groupName, session.getUser());
                    if (groupRepo.createGroup(group))
                        groupsPanel.add(new GroupBarController(session.getUser(), group).getBar());
                });
                groupCreationFrame.pack();
                groupCreationFrame.setVisible(true);
            });

            List<Group> groups = groupRepo.getGroups(session.getUser());
            for (var group : groups)
                groupsPanel.add(new GroupBarController(session.getUser(), group).getBar());
        }

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
                var searchFrame = new UserSearchFrame(btSearchForUsers);
                searchFrame.onSearch(searchString -> {
                    List<User>    users = userRepo.searchUser(searchString);
                    List<UserBar> bars  = new ArrayList<>(users.size());
                    for (var user : users) {
                        var bar = new UserBar(user);

                        // If the user isn't in our friend list,
                        // we either have or haven't sent a friend request to him.
                        // If we have, we'll show the "invite sent" button, greyed-out.
                        // If we haven't, we'll show the "ask to be friends" button,
                        // which when pressed sends the request to the user and
                        // also gets updated to the "request sent" button.
                        if (!session.getUser().equals(user) && !session.getFriends().contains(user)) {
                            var btSent = new JButton("Request sent");
                            btSent.setEnabled(false);

                            boolean alreadySentRequest = false;
                            List<Invite> invs = session.getInvites();
                            for (int i = 0; !alreadySentRequest && i < invs.size(); i++) {
                                if (invs.get(i).to().equals(user))
                                    alreadySentRequest = true;
                            }
                            if (alreadySentRequest) {
                                bar.addButton(btSent);
                            } else {
                                var btAsk = new JButton("Ask to be friends");
                                bar.addButton(btAsk);
                                btAsk.addActionListener(evt0 -> {
                                    var invite = new FriendshipInvite(session.getUser(), user, InviteState.PENDING);
                                    session.getInviteRepository().addInvite(invite);
                                    bar.removeButton(btAsk);
                                    bar.addButton(btSent);
                                    bar.repaint();
                                    bar.revalidate();
                                });
                            }
                        }
                        bars.add(bar);
                    }
                    return bars;
                });
                searchFrame.pack();
                searchFrame.setVisible(true);
            });

            var btInvites = new JButton("Invites");
            morePanel.add(btInvites);
            btInvites.addActionListener(evt -> {
                btInvites.setEnabled(false);
                var invitesFrame = new InviteListFrame(btInvites);
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
                var usgFrame = new PopUpFrame(btUsg);
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
