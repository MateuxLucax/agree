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
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.group.IGroupRepository;
import repositories.message.IMessageRepository;
import services.login.ILoginService;
import services.login.LoginService;
import utils.AssetsUtil;

import javax.swing.*;
import java.util.Date;
import java.util.LinkedList;

public class Application {

    private final JFrame frame;
    private UserSession session;
    private IMessageRepository msgRepo;
    private IGroupRepository groupRepo;

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

        authPanel.setLoginListener((name, password) -> {
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

        authPanel.setRegistrationListener((name, password) -> {
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

        for (var group : session.getGroups()) {
            GroupPanel groupPanel = createGroupPanel(group, groupTabs);
            groupTabs.addTab(group.getName(), groupPanel);
        }

        var groupCreationPanel = new CreateGroupPanel();
        groupTabs.addTab("+ New group", groupCreationPanel.getJPanel());

        groupCreationPanel.setCreationListener(groupName -> {
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
            friendPanel.add(new JLabel(friend.getNickname()));
            friendListPanel.addTab(friend.getNickname(), new JPanel());  // no UserPanel yet...
        }

        var mainPanel = new JTabbedPane(JTabbedPane.TOP);
        mainPanel.addTab("Groups", groupTabs);
        mainPanel.addTab("Friends", friendListPanel);
        // TODO "settings" tab

        frame.add(mainPanel);
    }

    // The role of these 'create' methods is to configure the behaviour
    // using the set...listener or setOn... methods provided by the panels

    public GroupPanel createGroupPanel(Group group, JTabbedPane groupTabs) {
        var groupPanel = new GroupPanel();

        MessagingPanel msgPanel = createMessagingPanel(group);
        groupPanel.setMessagingTab(msgPanel);

        UserListPanel membersPanel = createMembersPanel(group, groupTabs);
        groupPanel.setMembersTab(membersPanel);

        if (group.getOwner().equals(session.getUser())) {
            GroupManagementPanel managPanel = createGroupManagementPanel(group, groupPanel, groupTabs);
            groupPanel.setManagementTab(managPanel);
        }

        return groupPanel;
    }

    public MessagingPanel createMessagingPanel(Group group) {
        var msgPanel = new MessagingPanel();
        msgPanel.loadMessages(group.getMessages());
        msgPanel.setOnLoadOlder(() -> {
            LinkedList<Message> messages = group.getMessages();
            Date date = messages.isEmpty() ? new Date() : messages.getFirst().sentAt();
            msgRepo.getMessagesBefore(group, date);
            msgPanel.loadMessages(messages);
        });
        msgPanel.setOnLoadNewer(() -> {
            LinkedList<Message> messages = group.getMessages();
            Date date = messages.isEmpty() ? new Date() : messages.getLast().sentAt();
            msgRepo.getMessagesAfter(group, date);
            msgPanel.loadMessages(messages);
        });
        msgPanel.setOnSend(text -> {
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
        managPanel.setRenameButtonListener(newName -> {
            group.setName(newName);
            groupRepo.updateGroup(group);  // TODO consider just a .updateName method?
            // I'd like to change just the title of the tab
            // AFAIK this is the only way to do it -- replacing the whole tab
            int index = groupTabs.getSelectedIndex();
            groupTabs.removeTabAt(index);
            groupTabs.insertTab(group.getName(), null, groupPanel, null, index);
        });
        managPanel.setDeleteButtonListener(() -> {
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
