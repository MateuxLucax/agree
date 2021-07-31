package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import controllers.*;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        // Initialize the theme
        LafManager.install();
        LafManager.setTheme(new DarculaTheme());
        try {
            UIManager.setLookAndFeel(new DarculaThemeDarklafLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Start the session
        // TODO get rid of UserSession, or figure out what it really is in this application, it only exists here)
        //  maybe it centralizes access to the repositores or something?, so each Controller class
        //  doesn't need to instance its own? idk
        var session = UserSession.getInstance();
        var authCon = new AuthController();
        authCon.display();
        authCon.onSuccess(user -> {
            session.initialize(user);
            var mainCon = new MainController(session.getUser());
            mainCon.display();
            authCon.close();
        });
    }
}
