package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import gui.*;
import utils.AssetsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {

    private UserSession userSession;

    private final AppFrame frame;

    public Application() {
        LafManager.install();
        LafManager.setTheme(new DarculaTheme());
        try {
            UIManager.setLookAndFeel(new DarculaThemeDarklafLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        frame = new AppFrame("Agree");
        frame.setIconImage(AssetsUtil.getImage(AssetsUtil.ICON));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        startSession();
    }

    private void startSession() {
        var authPanel = new AuthPanel();
        frame.setMainPanel(authPanel.getJPanel());

        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        //frame.getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.pack();  // TODO change all the pack() calls to setSize, or maybe set to fullscreen
                       // or actually learn about Dimension, preferredSize and all that jazz

        frame.setVisible(true);

        authPanel.setSuccessListener(() -> {
            frame.setMainPanel(new JPanel());  // empty panel just to replace the authPanel
            userSession = UserSession.getInstance();
            initializeFrame();
            frame.revalidate();
        });
    }

    private void initializeFrame() {
        /*
                                         mainPanel
                   sideBar               VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
                   VVVVVVVVVVVVVVVVVVVVVV
                   +---------------------------------------------------------------------+
                   | > groups | friends | group 2 -- last message 10 min ago             |
                   +----------+---------+                                                |
        sidePanel {| group 1            | ...                                            |
                  {| > group 2          | dude_n (15 min ago): asdfasdf                  |
                  {| group 3            | dude_2 (14 min ago): goodbye                   |
                  {| ...                | dude_1 (10 min ago): hello world               |
                   +--------------------+                                                |
                   | create group       | Write message: _______________________________ |
                   +--------------------+------------------------------------------------+
        User clicks on "groups" or "friends" -> Contents of the sidePanel changes
        User clicks on a group, on a friend, or on "create group" -> Contents of the mainPanel change
        */

        var groupListPanel = new JPanel();
        groupListPanel.setLayout(new BoxLayout(groupListPanel, BoxLayout.PAGE_AXIS));

        for (var group : userSession.getGroups()) {
            var groupPanel = new GroupPanel(group);
            var groupBar = new GroupBar(group);
            groupBar.setOpenListener(evt -> {
                frame.setMainPanel(groupPanel.getJPanel());
                frame.revalidate();
                frame.pack();
            });
            groupListPanel.add(groupBar.getJPanel());
        }
        // FIXME when clicking on "open" in a GroupBar the group doesn't always immediatly open on the mainPanel, it's weird
        // More specifically, it opens immediatly the first time you click it,
        // but on further clicks you need to resize the window for it to happen

        var friendListPanel = new JPanel();
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.PAGE_AXIS));

        for (var friend : userSession.getFriends()) {
            var friendBar = new UserBar(friend);
            friendListPanel.add(friendBar.getJPanel());
        }

        var btGroups = new JButton("Groups");
        var btFriends = new JButton("Friends");
        btGroups.addActionListener(evt -> {
            frame.setSidePanel(groupListPanel);
            frame.revalidate();
        });
        btFriends.addActionListener(evt -> {
            frame.setSidePanel(friendListPanel);
            frame.revalidate();
        });

        var buttons = new JPanel(new FlowLayout());
        buttons.add(btGroups);
        buttons.add(btFriends);

        var btCreateGroup = new JButton("Create group");

        frame.addAboveSidePanel(buttons);
        frame.setSidePanel(groupListPanel);
        frame.addBelowSidePanel(btCreateGroup);
    }

    public static void main(String[] args) {
        var app = new Application();
    }
}
