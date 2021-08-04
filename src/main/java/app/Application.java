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

        var authCon = new AuthController();
        authCon.display();
        authCon.onSuccess(user -> {
            var mainCon = new MainController(user);
            mainCon.display();
            authCon.close();
        });
    }
}
