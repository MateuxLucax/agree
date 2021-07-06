package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import gui.*;
import models.User;
import models.UserGroupsMap;
import models.group.Group;
import models.invite.FriendshipInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import models.invite.InviteState;
import models.message.Message;
import repositories.group.IGroupRepository;
import repositories.message.IMessageRepository;
import repositories.user.IUserRepository;
import services.login.ILoginService;
import services.login.LoginService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Application {

    private UserSession session;
    private IMessageRepository msgRepo;
    private IGroupRepository groupRepo;
    private IUserRepository userRepo;

    public Application() {
        // Initialize theme
        LafManager.install();
        LafManager.setTheme(new DarculaTheme());
        try {
            UIManager.setLookAndFeel(new DarculaThemeDarklafLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        startUserSession();
    }

    private void startUserSession() {
        var authFrame = new JFrame();

        ILoginService loginService = new LoginService();
        var authPanel = new AuthPanel();
        authFrame.setContentPane(authPanel.getJPanel());

        authFrame.pack();
        authFrame.setVisible(true);

        session   = UserSession.getInstance();
        msgRepo   = session.getMessageRepository();
        groupRepo = session.getGroupRepository();
        userRepo  = session.getUserRepository();

        authPanel.onLogin((name, password) -> {
            if (name.isEmpty() || password.isEmpty()) {
                authPanel.warn("Username and password are required!");
                return;
            }
            try {
                session.initialize(loginService.authenticate(name, password));
                authFrame.dispose();
                initializeMainFrame();
            } catch (UnauthorizedUserException e) {
                authPanel.warn("Incorrect username or password!");
            }
        });

        authPanel.onRegistration((name, password) -> {
            if (name.isEmpty() || password.isEmpty()) {
                authPanel.warn("Username and password are required!");
                return;
            }
            try {
                var user = new User(name, password);
                if (loginService.createUser(user)) {
                    session.initialize(user);
                }
                authFrame.dispose();
                initializeMainFrame();
            } catch (NameAlreadyInUseException e) {
                authPanel.warn("Someone already uses the name " + name);
            } catch (UnsafePasswordException e) {
                authPanel.warn("Unsafe password!");
                // TODO tell what the password requirements are
                //     also, do it when the user moves the focus away from the password text field
            }
        });
    }

    // TODO move the main panel into a gui.MainPanel class
    //     but then where do the set...Listener calls go?
    //     probably better to wait and do it when we're supposed to use MVC
    private void initializeMainFrame() {
        var mainFrame = new JFrame();
        // The panels are created here in order of dependency

        //
        // Friend list panel
        //
        var friendsTabs = new JTabbedPane(JTabbedPane.LEFT);
        friendsTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        for (var friend : session.getFriends()) {
            friendsTabs.addTab(friend.getNickname(), createFriendPanel(friend));
        }

        // The request list panel and the group list panel
        // mutually depend on each other because of createGroupPanel calls
        // in both. So we needed to declare this variable above where
        // we actually add stuff to the invitesPanel
        var invitesPanel = new InvitesListPanel();

        //
        // Group list panel
        // (see createGroupPanel() for what each group panel looks like)
        //
        // For now the tabs only have text, but we can add icons to them
        // when we implement groups and users having their profile pictures:
        // https://stackoverflow.com/q/17648780
        var groupsTabs = new JTabbedPane(JTabbedPane.LEFT);
        groupsTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        session.getGroups().sort(Group.mostRecentActivityFirst());
        for (var group : session.getGroups()) {
            JTabbedPane groupPanel = createGroupPanel(group, groupsTabs, invitesPanel);
            groupsTabs.addTab(group.getName(), groupPanel);
            // TODO idenfity groups with unread messages somehow
        }


        //
        // Request list panel (friendship requests, group invites)
        //
        for (var req : session.getInvites()) {
            var reqBar = new InvitesBar(req, session.getUser());
            invitesPanel.addInvite(reqBar);
            if (req.getState() == InviteState.PENDING && req.to().equals(session.getUser())) {
                reqBar.onAccept(() -> {
                    req.setState(InviteState.ACCEPTED);
                    // TODO actually update request in the database
                    if (req instanceof FriendshipInvite) {
                        // TODO actually add friendship relationship in the database
                        // Now we need to update the friend list panel to also show this new friend
                        User newFriend = req.from();
                        friendsTabs.addTab(newFriend.getNickname(), createFriendPanel(newFriend));
                    } else {  // req instanceof GroupInvite
                        // TODO actually add user to the group in the database
                        // Now we need to make this group available to the user,
                        // who just got invited to it and accepted,
                        // which involves creating its groupPanel and also its tab in the groupsTabs panel
                        var invite = (GroupInvite) req;
                        Group group = invite.getGroup();
                        invite.getGroup().addUser(req.to());
                        JTabbedPane groupPanel = createGroupPanel(group, groupsTabs, invitesPanel);
                        groupsTabs.insertTab(
                                group.getName(),
                                null,
                                createGroupPanel(group, groupsTabs, invitesPanel),
                                null,
                                groupsTabs.getTabCount() - 1
                        );
                    }
                });
                reqBar.onDecline(() -> {
                    req.setState(InviteState.DECLINED);
                    // TODO actually update request in the database
                });
            }
        }

        //
        // Panel for creating a new group
        //
        var groupCreationPanel = new CreateGroupPanel();
        groupsTabs.addTab("+ New group", groupCreationPanel.getJPanel());

        groupCreationPanel.onCreation(groupName -> {
            var group = new Group(groupName, session.getUser());
            if (groupRepo.createGroup(group)) {
                // insert the new tab before the "+ New group" one, so it's always last
                groupsTabs.insertTab(
                        group.getName(),
                        null,
                        createGroupPanel(group, groupsTabs, invitesPanel),
                        null,
                        groupsTabs.getTabCount() - 1
                );
                // TODO extract the groupsTabs stuff to its own class,
                //    so we can more easily add a new tab into it withuot
                //    having to worry that it can't take the place of the
                //    "+ New group" tab as the last tab
            }
        });

        //
        // User search panel
        //
        var userSearchPanel = new UserSearchPanel();
        // TODO consider if maybe this whole lambda should be a class of its own or something, given its size
        userSearchPanel.onSearch(searchString -> {
            List<User>    users = userRepo.searchUser(searchString);
            List<UserBar> bars  = new ArrayList<>(users.size());
            for (var user : users) {
                var bar = new UserBar(user);

                // If the user isn't in our friend list,
                // we either have or haven't sent a friend request to him.
                // If we have, we'll show the "request sent" button, greyed-out.
                // If we haven't, we'll show the "ask to be friends" button,
                // which when pressed sends the request to the user and
                // also gets updated to the "request sent" button.
                if (!session.getUser().equals(user) && !session.getFriends().contains(user)) {
                    var btSent = new JButton("Request sent");
                    btSent.setEnabled(false);

                    /* NOTE: For when we have a database,
                       maybe the search should do a SELECT query returning something like
                       | nickname | isFriend | alreadySentFriendRequest |
                       which would be more efficient(?)
                       Basically, some extra information along with the list of users who match the search,
                       so we don't need to do all this computation to check if it's a friend and if we have sent a request
                     */

                    boolean alreadySentRequest = false;
                    List<Invite> reqs = session.getInvites();
                    for (int i = 0; !alreadySentRequest && i < reqs.size(); i++) {
                        if (reqs.get(i).to().equals(user))
                            alreadySentRequest = true;
                    }
                    if (alreadySentRequest) {
                        bar.addButton(btSent);
                    } else {
                        var btAsk = new JButton("Ask to be friends");
                        bar.addButton(btAsk);
                        btAsk.addActionListener(evt -> {
                            var invite = new FriendshipInvite(session.getUser(), user, InviteState.PENDING);
                            session.getInviteRepository().addInvite(invite);
                            invitesPanel.addInvite(new InvitesBar(invite, session.getUser()));
                            bar.removeButton(btAsk);
                            bar.repaint();
                            bar.addButton(btSent);
                        });

                    }

                }
                bars.add(bar);
            }
            return bars;
        });

        //
        // Panel listing the users who belong to the same groups as you
        //

        // "USG": [U]sers in the [S]ame [G]roup as you
        // (couldn't think of a shorter term for this, so I made up an acronym to avoid really long variable names)
        // TODO extract this to its own panel
        var usgScrollPane = new JScrollPane();
        usgScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        var usgPanel = new JPanel();
        usgScrollPane.setViewportView(usgPanel);
        usgPanel.setLayout(new BoxLayout(usgPanel, BoxLayout.PAGE_AXIS));
        /* We previously used a Set to show all the users who belong to the same
         * groups as the user in session without showing duplicates when some user
         * belongs to more than one group in common.
         * But then the user seeing this list won't know which groups in common those are.
         * So now instead we use a Map<User, List<Group>> to show, for each user,
         * what are the groups they have in common with you.
         * The previous Set implementation is commented below.
        Set<User> usgSet = new HashSet<>();
        for (var group : session.getGroups()) {
            usgSet.addAll(group.getUsers());
        }
        for (var user : usgSet) {
            var bar = new UserBar(user);
            usgPanel.add(bar);
        }
        */
        var usgMap = new UserGroupsMap();
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
            var strGroups = new StringBuilder();
            strGroups.append(user.getNickname()).append(" belongs to ");
            strGroups.append(groups);

            usgPanel.add(new JLabel(strGroups.toString()));
            // Just in a JLabel for now
            // TODO "ask to be friends" button
            //     which would require this functionality to be extracted to a method and called here
            //     otherwise it'd be implemented twice -- here and in the user search panel
        }

        // Make all those panels available in the main/home panel by tabs
        var mainPanel = new JTabbedPane(JTabbedPane.TOP);
        mainPanel.addTab("Groups", groupsTabs);
        mainPanel.addTab("Friends", friendsTabs);
        mainPanel.addTab("Search", userSearchPanel);
        mainPanel.addTab("Invites", invitesPanel);
        mainPanel.addTab("Users in the same groups as you", usgScrollPane);
        // TODO "settings" tab

        mainFrame.setContentPane(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    // We had to extract createGroupPanel to a method because it needed to be done twice in the code:
    // when we load the user's groups, and when the user creates another group.
    // And from that it just seemed nice to also extract the procedures that create the group's subpanels
    // to their own methods too.
    // The role of these 'create' methods is to configure the behaviour
    // using the set...listener or setOn... methods provided by the panels

    public JTabbedPane createGroupPanel(Group group, JTabbedPane groupTabs, InvitesListPanel reqPanel) {
        var groupPanel = new JTabbedPane();

        groupPanel.addTab("Messages", createGroupMessagingPanel(group));
        groupPanel.addTab("Members", createMembersPanel(group, groupTabs, reqPanel));
        groupPanel.addTab("Invite friends", createGroupInvitePanel(group, reqPanel));

        if (group.getOwner().equals(session.getUser())) {
            groupPanel.addTab("Manage", createGroupManagementPanel(group, groupPanel, groupTabs).getJPanel());
        }

        return groupPanel;
    }

    public MessagingPanel createGroupMessagingPanel(Group group) {
        var msgPanel = new MessagingPanel();
        msgPanel.loadMessages(group.getMessages());
        msgPanel.onLoadOlder(() -> {
            msgRepo.getMessagesBefore(group, group.oldestMessageDate());
            msgPanel.loadMessages(group.getMessages());
        });
        msgPanel.onLoadNewer(() -> {
            msgRepo.getMessagesAfter(group, group.newestMessageDate());
            msgPanel.loadMessages(group.getMessages());
        });
        msgPanel.onSendMessage(text -> {
            var msg = new Message(session.getUser(), text, new Date());
            boolean ok = msgRepo.addMessage(group, msg);
            if (ok) {
                group.loadMessageBelow(msg);
                msgPanel.loadMessages(group.getMessages());
            }
            return ok;
        });
        return msgPanel;
    }

    public GroupManagementPanel createGroupManagementPanel(Group group, JTabbedPane groupPanel, JTabbedPane groupTabs) {
        var managPanel = new GroupManagementPanel(group.getName());
        managPanel.onRename(newName -> {
            group.setName(newName);
            groupRepo.updateGroup(group);  // TODO consider just a .updateName method?
            // I'd like to change just the title of the tab
            // AFAIK this is the only way to do it -- replacing the whole tab
            int index = groupTabs.getSelectedIndex();
            groupTabs.removeTabAt(index);
            groupTabs.insertTab(group.getName(), null, groupPanel, null, index);
        });
        managPanel.onDelete(() -> {
            groupTabs.removeTabAt(groupTabs.getSelectedIndex());
            groupRepo.removeGroup(group.getId());
        });
        return managPanel;
    }

    public UserListPanel createMembersPanel(Group group, JTabbedPane groupTabs, InvitesListPanel reqPanel) {
        var membersPanel = new UserListPanel();

        var ownerBar = new UserBar(group.getOwner());
        var btOwner = new JButton("Owner");
        btOwner.setEnabled(false);
        ownerBar.addButton(btOwner);
        membersPanel.addUserBar(ownerBar);

        for (var user : group.getUsers()) {
            var bar = new UserBar(user);
            if (session.getUser().equals(group.getOwner())) {
                JButton btRemove = new JButton("Remove");
                bar.addButton(btRemove);
                btRemove.addActionListener(evt -> {
                    group.removeUser(user);
                    // TODO actually remove user from group in the database
                    membersPanel.removeUserBar(bar);
                });

                JButton btSetOwner = new JButton("Set owner");
                bar.addButton(btSetOwner);
                btSetOwner.addActionListener(evt -> {
                    // TODO actually update owner in the database

                    group.changeOwner(user);
                    // Recreate the GroupPanel, now with the other user as owner
                    JTabbedPane groupPanel = createGroupPanel(group, groupTabs, reqPanel);
                    // Replace the tab
                    int index = groupTabs.getSelectedIndex();
                    groupTabs.removeTabAt(index);
                    groupTabs.insertTab(group.getName(), null, groupPanel, null, index);
                });
            }
            membersPanel.addUserBar(bar);
        }
        return membersPanel;
    }

    public GroupInvitePanel createGroupInvitePanel(Group group, InvitesListPanel reqPanel) {
        var panel = new GroupInvitePanel();
        for (var friend : session.getFriends()) {
            // Don't show friends who already are in the group
            if (group.isMember(friend))
                continue;
            // Check whether we've already invited this friend to this group
            boolean alreadySentInvite = false;
            List<Invite> reqs = session.getInvites();
            for (int i = 0; !alreadySentInvite && i < reqs.size(); i++) {
                Invite req = reqs.get(i);
                if (req instanceof GroupInvite && req.to().equals(friend))
                    alreadySentInvite = true;
            }
            // If we've already sent a group invite to this friend,
            // then don't show the "Invite to group" button, but rather
            // the greyed-out "Invite sent" button
            var bar = new UserBar(friend);
            panel.addBar(bar);
            var btSent = new JButton("Invite sent");
            btSent.setEnabled(false);
            if (alreadySentInvite) {
                bar.addButton(btSent);
            } else {
                var btn = new JButton("Invite to group");
                bar.addButton(btn);
                btn.addActionListener(evt -> {
                    var invite = new GroupInvite(session.getUser(), friend, InviteState.PENDING, group);
                    session.getInvites().add(invite);
                    // TODO actually add request to the database
                    var reqBar = new InvitesBar(invite, session.getUser());
                    reqPanel.addInvite(reqBar);
                    bar.removeButton(btn);
                    bar.repaint();
                    bar.addButton(btSent);
                });
            }
        }
        return panel;
    }

    // Again, we needed to create a new friend panel in two places
    // (at startup, when we create the panels for each of the user's current friends
    // and when the user accepts a friendship request from someone else),
    // and thus this needed to be extracted to its own method
    public JPanel createFriendPanel(User friend) {
        // Although we don't know yet what we put in a friend panel
        // Maybe a MessagingPanel for private messages
        // For now we just have this placeholder panel
        var lb = new JLabel(friend.getNickname());
        var pn = new JPanel();
        pn.add(lb);
        return pn;
    }


    public static void main(String[] args) {
        new Application();
    }
}
