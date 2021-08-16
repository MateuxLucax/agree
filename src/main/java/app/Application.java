package app;

import app.config.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import controllers.AuthController;
import controllers.MainController;
import exceptions.InvalidConfigFileException;
import exceptions.ParserErrorConfigurationFileException;
import repositories.DBConnection;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Application {

    public static Config config;

    private Application() {
        this.setUp();
    }

    private void setUp() {
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

    private static void setUpConfigAttributes() {
        DBConnection.setUp(Application.config.getDatabase());
    }

    private static void initConfig(String configFile) throws ParserErrorConfigurationFileException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Application.config = mapper.readValue(new File(configFile), Config.class);
        } catch (IOException e) {
            throw new ParserErrorConfigurationFileException("Can't parse configuration file because: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InvalidConfigFileException, ParserErrorConfigurationFileException {
        if (args.length == 0) {
            throw new InvalidConfigFileException("Configuration file not specified.");
        }
        Application.initConfig(args[0]);
        Application.setUpConfigAttributes();

        new Application();
    }
}
