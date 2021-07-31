package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import controllers.*;
import controllers.group.GroupListController;

import javax.swing.*;

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
        var authCon = new AuthController();
        authCon.display();
        authCon.onSuccess(user -> {
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

        var moreCon = new MoreController(session.getUser());
        mainPane.addTab("More", moreCon.getPanel());

        mainFrame.setContentPane(mainPane);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new Application();
    }
}
