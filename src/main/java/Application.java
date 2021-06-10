import data.UserDataAccess;
import exceptions.UnsafePasswordException;
import gui.AuthPanel;
import models.User;
import models.group.Group;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final UserDataAccess userDataAccess = UserDataAccess.getInstance();

    // Information about the user in session
    private User loggedUser;
    private List<Group> loggedUserGroups;
    private List<User> loggedUserFriends;

    private JFrame frame;

    public Application() {
        this.loggedUserGroups = new ArrayList<>();
        this.loggedUserFriends = new ArrayList<>();

        this.frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        startSession();
    }

    private void startSession() {
        var authPanel = new AuthPanel();
        frame.add(authPanel.getPanel());

        authPanel.setLoginListener(username -> {
            loggedUser = userDataAccess.retrieveUser(username);
            loggedUserFriends = userDataAccess.retrieveFriends(loggedUser);
            loggedUserGroups = userDataAccess.retrieveGroups(loggedUser);
        });

        authPanel.setRegistrationListener((username, password) -> {
            userDataAccess.createAccount(username, password);
            loggedUser = userDataAccess.retrieveUser(username);
            loggedUserFriends = userDataAccess.retrieveFriends(loggedUser);
            loggedUserGroups = userDataAccess.retrieveGroups(loggedUser);
        });

    }


    /*public static Application startSession()
    throws IncorrectPasswordException,
           NameAlreadyInUseException,
           UnsafePasswordException
    {
        // (For now we'll use a Scanner, just as a placeholder)
        Scanner scan = new Scanner(System.in);

        System.out.println("Login (type 1) or create account (type 2)?");
        int action = scan.nextInt();

        System.out.print("What's your name? ");
        String name = scan.next();
        System.out.print("What's your password? ");
        String password = scan.next();
        Application app;
        if (action == 1) {
            userDataAccess.authenticate(name, password);
            User user = userDataAccess.retrieveUser(name);
            app = new Application(user);
            app.loggedUserGroups.addAll(userDataAccess.retrieveGroups(user));
            app.loggedUserFriends.addAll(userDataAccess.retrieveFriends(user));
        } else {
            userDataAccess.createAccount(name, password);
            User user = userDataAccess.retrieveUser(name);
            app = new Application(user);
        }

        return app;
    }*/

    public static void main(String[] args) {

        var app = new Application();

        /* try {
            Application app = Application.startSession();
            System.out.println("Welcome!");


            System.out.println("\nYour groups:");
            app.loggedUserGroups.sort(Comparator.comparing(Group::getLastMessageDate));
            System.out.println(app.loggedUserGroups);

            System.out.println("\nYour friends:");
            System.out.println(app.loggedUserFriends);

        } catch (IncorrectPasswordException e) {
            System.out.println("Invalid credentials for user " + e.getUsername());
        } catch (UnsafePasswordException e) {
            System.out.println("Your password is unsafe -- too short, lacking numbers etc.");
        } catch (NameAlreadyInUseException e) {
            System.out.println("The name " + e.getName() + " is already being used by someone else");
        } catch(Exception e) {
            e.printStackTrace();
        } */
    }
}
