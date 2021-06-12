package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import gui.AuthPanel;
import gui.GroupBar;
import gui.GroupPanel;
import gui.UserBar;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;
import utils.AssetsUtil;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

public class Application {

    private UserSession userSession;

    private final JFrame frame;
    private final JPanel homePanel;

    public Application() {
        LafManager.install();
        LafManager.setTheme(new DarculaTheme());
        try {
            UIManager.setLookAndFeel(new DarculaThemeDarklafLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        frame = new JFrame("Agree");
        frame.setIconImage(AssetsUtil.getImage(AssetsUtil.ICON));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        homePanel = new JPanel();

        startSession();
    }

    private void startSession() {

        IGroupRepository groupRepository = new GroupInFileRepository();

        User user1 = new User("cleber", new Date());
        User user2 = new User("carlos", new Date());
        Group group = new Group("teste");
        group.addUser(user1);
        group.addUser(user2);

        groupRepository.createGroup(group);

        var authPanel = new AuthPanel();
        frame.add(authPanel.getJPanel());
        frame.pack();
        frame.setVisible(true);

        authPanel.setSuccessListener(() -> {
            userSession = UserSession.getInstance();
            populateHomePanel();
            frame.remove(authPanel.getJPanel());
            frame.add(homePanel);
            frame.pack();
        });
    }

    // Precondition: user is logged in,
    // loggedUserGroups and loggedUserFriends are initialized
    private void populateHomePanel() {
        var groupsPanel = new JPanel();
        var friendsPanel = new JPanel();

        for (var group : userSession.getGroups()) {
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
        for (var friend : userSession.getFriends())
            friendsPanel.add(new UserBar(friend).getJPanel());

        homePanel.add(groupsPanel);
        homePanel.add(friendsPanel);
    }

    public static void main(String[] args) {
        var app = new Application();
    }
}
