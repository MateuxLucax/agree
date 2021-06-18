package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import gui.AuthPanel;
import gui.GroupPanel;
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
        //frame.getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.pack();  // TODO change all the pack() calls to setSize, or maybe set to fullscreen
                       // or actually learn about Dimension, preferredSize and all that jazz

        frame.setVisible(true);

        authPanel.setSuccessListener(() -> {
            frame.remove(authPanel.getJPanel());
            userSession = UserSession.getInstance();
            initializeFrame();
            frame.pack();
            frame.revalidate();
        });
    }

    private void initializeFrame() {
        // For now the tabs only have text, but we can add icons to them
        // when we implement groups and users having their profile pictures:
        // https://stackoverflow.com/q/17648780
        var groupListPanel = new JTabbedPane(JTabbedPane.LEFT);
        for (var group : userSession.getGroups()) {
            groupListPanel.addTab(group.getName(), new GroupPanel(group).getJPanel());
        }

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
