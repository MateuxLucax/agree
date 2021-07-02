package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import gui.*;
import gui.GroupManagementPanel;
import gui.MessagingPanel;
import models.request.FriendshipRequest;
import models.request.GroupInvite;
import models.request.Request;
import models.request.RequestState;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.group.IGroupRepository;
import repositories.message.IMessageRepository;
import repositories.user.IUserRepository;
import services.login.ILoginService;
import services.login.LoginService;
import utils.AssetsUtil;

import javax.swing.*;
import java.util.*;

public class Application {

    private final JFrame frame;
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

        frame = new JFrame("Agree");
        frame.setIconImage(AssetsUtil.getImage(AssetsUtil.ICON));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        startUserSession();
    }

    private void startUserSession() {
        ILoginService loginService = new LoginService();
        var authPanel = new AuthPanel();
        frame.add(authPanel.getJPanel());

        frame.pack();
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

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
                frame.remove(authPanel.getJPanel());
                initializeMainPanel();
                frame.revalidate();
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
                frame.remove(authPanel.getJPanel());
                initializeMainPanel();
                frame.revalidate();
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
    private void initializeMainPanel() {
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
        // we actually add stuff to the requestListPanel
        var requestListPanel = new RequestListPanel();

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
            JTabbedPane groupPanel = createGroupPanel(group, groupsTabs, requestListPanel);
            groupsTabs.addTab(group.getName(), groupPanel);
            // TODO idenfity groups with unread messages somehow
        }


        //
        // Request list panel (friendship requests, group invites)
        //
        for (var req : session.getRequests()) {
            var reqBar = new RequestBar(req, session.getUser());
            requestListPanel.addRequest(reqBar);
            if (req.getState() == RequestState.PENDING && req.to().equals(session.getUser())) {
                reqBar.onAccept(() -> {
                    req.setState(RequestState.ACCEPTED);
                    // TODO actually update request in the database
                    if (req instanceof FriendshipRequest) {
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
                        JTabbedPane groupPanel = createGroupPanel(group, groupsTabs, requestListPanel);
                        groupsTabs.insertTab(
                                group.getName(),
                                null,
                                createGroupPanel(group, groupsTabs, requestListPanel),
                                null,
                                groupsTabs.getTabCount() - 1
                        );
                    }
                });
                reqBar.onDecline(() -> {
                    req.setState(RequestState.DECLINED);
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
                        createGroupPanel(group, groupsTabs, requestListPanel),
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
                    List<Request> reqs = session.getRequests();
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
                            var request = new FriendshipRequest(session.getUser(), user, RequestState.PENDING);
                            session.getRequests().add(request);
                            // TODO actually add the request to the db / requestRepository
                            requestListPanel.addRequest(new RequestBar(request, session.getUser()));
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
        // TODO extract to its own panel
        var usgScrollPane = new JScrollPane();
        usgScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        var usgPanel = new JPanel();
        usgScrollPane.setViewportView(usgPanel);
        // We use a Set to ensure that a user who belongs to more
        // than one group in common appears only once in here
        Set<User> usgSet = new HashSet<>();
        for (var group : session.getGroups()) {
            usgSet.addAll(group.getUsers());
        }
        for (var user : usgSet) {
            var bar = new UserBar(user);
            usgPanel.add(bar);
            // TODO "add friend" button
            //    then this "add friend" functionality is implemented in two places, here and in the search panel
        }

        // Make all those panels available in the main/home panel by tabs
        var mainPanel = new JTabbedPane(JTabbedPane.TOP);
        mainPanel.addTab("Groups", groupsTabs);
        mainPanel.addTab("Friends", friendsTabs);
        mainPanel.addTab("Search", userSearchPanel);
        mainPanel.addTab("Requests", requestListPanel);
        mainPanel.addTab("Users in the same groups as you", usgScrollPane);
        // TODO "settings" tab

        frame.add(mainPanel);
    }

    // We had to extract createGroupPanel to a method because it needed to be done twice in the code:
    // when we load the user's groups, and when the user creates another group.
    // And from that it just seemed nice to also extract the procedures that create the group's subpanels
    // to their own methods too.
    // The role of these 'create' methods is to configure the behaviour
    // using the set...listener or setOn... methods provided by the panels

    public JTabbedPane createGroupPanel(Group group, JTabbedPane groupTabs, RequestListPanel reqPanel) {
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
            LinkedList<Message> messages = group.getMessages();
            Date date = messages.isEmpty() ? new Date() : messages.getFirst().sentAt();
            msgRepo.getMessagesBefore(group, date);
            msgPanel.loadMessages(messages);
        });
        msgPanel.onLoadNewer(() -> {
            LinkedList<Message> messages = group.getMessages();
            Date date = messages.isEmpty() ? new Date() : messages.getLast().sentAt();
            msgRepo.getMessagesAfter(group, date);
            msgPanel.loadMessages(messages);
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

    public UserListPanel createMembersPanel(Group group, JTabbedPane groupTabs, RequestListPanel reqPanel) {
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
                    group.setOwner(user);
                    // TODO actually update owner in the database

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

    public GroupInvitePanel createGroupInvitePanel(Group group, RequestListPanel reqPanel) {
        var panel = new GroupInvitePanel();

        // TODO 1. don't show "invite to group" button for users already in the group,
        //         and also not for the owner;
        //         actually, just don't show them in the list!
        //      2. don't show "invite to group" button for users for whom an invite has already been sent
        //         instead show the "invite sent" button
        //      again, doing a database query returning something like
        //      | nickname | inGroup | groupInviteSent |
        //      would be *very* useful here,
        //      but, for now, we could just search in the userSession lists as we do in the user search panel

        for (var friend : session.getFriends()) {
            var bar = new UserBar(friend);
            panel.addBar(bar);
            var btn = new JButton("Invite to group");
            bar.addButton(btn);
            btn.addActionListener(evt -> {
                var invite = new GroupInvite(session.getUser(), friend, RequestState.PENDING, group);
                session.getRequests().add(invite);
                // TODO actually add request to the database
                var reqBar = new RequestBar(invite, session.getUser());
                reqPanel.addRequest(reqBar);
                bar.removeButton(btn);
                bar.repaint();
                var btSent = new JButton("Invite sent");
                btSent.setEnabled(false);
                bar.addButton(btSent);
            });
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
