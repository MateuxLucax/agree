import data.UserDataAccess;
import exceptions.IncorrectPasswordException;
import exceptions.NameAlreadyInUseException;
import exceptions.UnsafePasswordException;
import models.User;
import models.group.Group;

import java.util.*;

public class Application {

    // Information about the user in session
    private final User loggedUser;
    private final List<Group> loggedUserGroups;
    private final List<User> loggedUserFriends;

    private final static UserDataAccess userDataAccess = UserDataAccess.getInstance();

    public Application(User user) {
        this.loggedUser = user;
        this.loggedUserGroups = new ArrayList<>();
        this.loggedUserFriends = new ArrayList<>();
    }

    // Why all the custom exceptions? Couldn't we just use Exception or RuntimeException?
    // In this particular case, the client code (say, UI code) that calls startSession actually wants
    // to know precisely what happened, so it can say to the user "hey, this name is already in use" or
    // "that password is incorrect, try again".
    // If we just throw a RuntimeException or have the methods returning bool that isn't possible
    // (Well, sure, authenticateUser returning false surely means the password is incorrect,
    // but createAccount returning false could mean either that the name is already in use
    // or that the password is unsafe -- to short, no numbers, no uppercase letters etc.
    // [although we might not actually care about this aspect of the software...],
    // so we can't know specifically what happened)

    // Also, we probably need two different methods here, one for login and one for registration

    public static Application startSession()
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
    }

    public static void main(String[] args) {
        try {
            Application app = Application.startSession();
            System.out.println("Welcome!");

            // Comparator<Group> byLastMessageDate = Comparator.comparing(Group::getLastMessageDate);

            System.out.println("\nYour groups:");
            // app.loggedUserGroups.sort(byLastMessageDate);  // NullPointerException here...
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
        }
    }
}
