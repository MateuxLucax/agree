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

        // TODO add refresh button to Group and Friends tabs in the main frame,
        //   because since they're not frames the user won't be able to just
        //   close them and open them again to get the updated information
        //   (maybe even on the frames this button should be present,
        //   since it's more convenient than closing and opening)

        // TODO add to the frame titles the nickname of the user logged in

        var authCon = new AuthController();
        authCon.display();
        authCon.onSuccess(user -> {
            var mainCon = new MainController(user);
            mainCon.display();
            authCon.close();
        });
    }
}
