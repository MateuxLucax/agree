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
import models.User;
import models.group.Group;
import models.message.Message;
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

        session = UserSession.getInstance();
        msgRepo = session.getMessageRepository();

        authPanel.setLoginListener((name, password) -> {
            if (name.isEmpty() || password.isEmpty()) {
                authPanel.warn("Username and password are required!");
                return;
            }
            try {
                session.setUser(loginService.authenticate(name, password));
                session.initialize();
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
                    session.setUser(user);
                    session.initialize();
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
        var groupListPanel = new JTabbedPane(JTabbedPane.LEFT);
        for (var group : session.getGroups()) {
            boolean isOwner = session.getUser().equals(group.getOwner());
            var groupPanel = new GroupPanel(group, isOwner);

            groupPanel.setLoadOlderButtonListener(evt -> {
                LinkedList<Message> messages = group.getMessages();
                Date date = messages.isEmpty() ? new Date() : messages.getFirst().sentAt();
                msgRepo.getMessagesBefore(group, date);
                groupPanel.refreshMessageListPanel();
            });

            groupPanel.setLoadNewerButtonListener(evt -> {
                LinkedList<Message> messages = group.getMessages();
                Date date = messages.isEmpty() ? new Date() : messages.getLast().sentAt();
                msgRepo.getMessagesAfter(group, date);
                groupPanel.refreshMessageListPanel();
            });

            groupPanel.setSendButtonListener(text -> {
                var msg = new Message(session.getUser(), text, new Date());
                boolean sentSuccessfully = msgRepo.addMessage(group, msg);
                if (sentSuccessfully) {
                    group.loadMessageBelow(msg);
                    groupPanel.refreshMessageListPanel();
                }
                return sentSuccessfully;
            });

            groupListPanel.addTab(group.getName(), groupPanel.getJPanel());
        }

        var groupCreationPanel = new CreateGroupPanel();
        groupListPanel.addTab("+ New group", groupCreationPanel.getJPanel());

        groupCreationPanel.setCreationListener(groupName -> {
            var group = new Group(groupName, session.getUser());
            session.getGroupRepository().createGroup(group);
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
        for (var friend : session.getFriends()) {
            var friendPanel = new JPanel();
            // not sure what to put here yet
            // if having someone as a friend automatically creates a group with just you and the friend only
            // then here we'll show a GroupPanel with said group
            friendPanel.add(new JLabel(friend.getNickname()));
            friendListPanel.addTab(friend.getNickname(), new JPanel());  // no UserPanel yet...
        }

        var mainPanel = new JTabbedPane(JTabbedPane.TOP);
        mainPanel.addTab("Groups", groupListPanel);
        mainPanel.addTab("Friends", friendListPanel);
        // TODO "settings" tab

        frame.add(mainPanel);
    }

    public static void main(String[] args) {
        var app = new Application();
    }
}
