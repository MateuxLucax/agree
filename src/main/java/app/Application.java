package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import gui.*;
import gui.GroupManagementPanel;
import gui.GroupPanel;
import gui.MessagingPanel;
import models.FriendshipRequest;
import models.Request;
import models.RequestState;
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
        // For now the tabs only have text, but we can add icons to them
        // when we implement groups and users having their profile pictures:
        // https://stackoverflow.com/q/17648780
        var groupTabs = new JTabbedPane(JTabbedPane.LEFT);

        // TODO order the groups with a Comparator that compares by last message date,
        //     thus showing first the groups with most recent activity

        for (var group : session.getGroups()) {
            GroupPanel groupPanel = createGroupPanel(group, groupTabs);
            groupTabs.addTab(group.getName(), groupPanel);
        }

        var groupCreationPanel = new CreateGroupPanel();
        groupTabs.addTab("+ New group", groupCreationPanel.getJPanel());

        groupCreationPanel.onCreation(groupName -> {
            var group = new Group(groupName, session.getUser());
            if (groupRepo.createGroup(group)) {
                // insert the new tab before the "+ New group" one, so it's always last
                groupTabs.insertTab(
                        group.getName(),
                        null,
                        createGroupPanel(group, groupTabs),
                        null,
                        groupTabs.getTabCount() - 1
                );
            }
        });

        var friendListPanel = new JTabbedPane(JTabbedPane.LEFT);
        for (var friend : session.getFriends()) {
            var friendPanel = new JPanel();
            // not sure what to put here yet
            // if having someone as a friend automatically creates a group with just you and the friend only
            // then here we'll show a GroupPanel with said group
            // TODO button to remove friend
            friendPanel.add(new JLabel(friend.getNickname()));
            friendListPanel.addTab(friend.getNickname(), friendPanel);  // no UserPanel yet...
        }

        var userSearchPanel = new UserSearchPanel();
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
                            // TODO actually add the request to the db / requestRepository?
                            // TODO update the panel which will present the requests to/from the user to show this new one
                            // Replace the "ask" button with the "request sent" button (TODO only do this if the request really succeded)
                            bar.removeButton(btAsk);
                            bar.repaint();
                            bar.addButton(btSent);
                        });

                    }

                }
                bars.add(bar);
            }
            userSearchPanel.loadResults(bars);
        });

        var mainPanel = new JTabbedPane(JTabbedPane.TOP);
        mainPanel.addTab("Groups", groupTabs);
        mainPanel.addTab("Friends", friendListPanel);
        mainPanel.addTab("Search", userSearchPanel);
        // TODO "settings" tab

        frame.add(mainPanel);
    }

    // The role of these 'create' methods is to configure the behaviour
    // using the set...listener or setOn... methods provided by the panels

    public GroupPanel createGroupPanel(Group group, JTabbedPane groupTabs) {
        var groupPanel = new GroupPanel();

        MessagingPanel msgPanel = createGroupMessagingPanel(group);
        groupPanel.setMessagingTab(msgPanel);

        UserListPanel membersPanel = createMembersPanel(group, groupTabs);
        groupPanel.setMembersTab(membersPanel);

        if (group.getOwner().equals(session.getUser())) {
            GroupManagementPanel managPanel = createGroupManagementPanel(group, groupPanel, groupTabs);
            groupPanel.setManagementTab(managPanel);
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

    public GroupManagementPanel createGroupManagementPanel(Group group, GroupPanel groupPanel, JTabbedPane groupTabs) {
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

    public UserListPanel createMembersPanel(Group group, JTabbedPane groupTabs) {
        var membersPanel = new UserListPanel();
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
                    GroupPanel groupPanel = createGroupPanel(group, groupTabs);
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

    public static void main(String[] args) {
        new Application();
    }
}
