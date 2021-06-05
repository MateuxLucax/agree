import models.User;
import models.group.Group;

import javax.naming.AuthenticationException;
import java.util.*;

public class Application {

    // Information about the user in session
    private final User loggedUser;
    private final List<Group> loggedUserGroups;
    private final List<User> loggedUserFriends;

    public Application(User user) {
        this.loggedUser = user;
        this.loggedUserGroups = new ArrayList<>();
        this.loggedUserFriends = new ArrayList<>();
    }

    // The methods below are just placeholders for actual database access methods, which we'll do later

    private static boolean authenticateUser(String name, String password) {
        String thePassword = "123";
        return password.equals(thePassword);
    }

    private static User retrieveUser(String name) {
        return new User(name, new Date());
    }

    private static List<Group> retrieveUsersGroups(User user) {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group("group 1"));
        groups.add(new Group("group 2"));
        groups.add(new Group("last group"));
        return groups;
    }

    private static List<User> retrieveUsersFriends(User user) {
        List<User> friends = new ArrayList<User>();
        friends.add(new User("john123", new Date()));
        friends.add(new User("asdf", new Date()));
        friends.add(new User("aeiou69", new Date()));
        return friends;
    }

    // Database access encapsulated like this?
    // class UserDataAccess
    //   + boolean authenticateUser(String name, String password)
    //   + User retrieve(String name)  <- static constructor for creating a user instance already populated with database info (like creationDate)
    //   + List<Group> retrieveGroups(User user)
    //   + List<User>  retrieveFriends(User user)
    //   ^ all of them static
    //     or maybe it's a singleton, since it needs to be instantiated in order to take a database connection, if I remember correctly how DB interaction works
    // Application
    //   - UserDataProvider = UserDataProvider.getInstance()

    public static Application startSession() throws AuthenticationException {
        // (For now we'll use a Scanner, again just as a placeholder)

        Scanner scan = new Scanner(System.in);
        System.out.print("What's your name? ");
        String name = scan.next();
        System.out.print("What's your password? ");
        String password = scan.next();

        if (!authenticateUser(name, password))
            throw new AuthenticationException("Could not authenticate user");

        User user = retrieveUser(name);
        Application app = new Application(user);

        app.loggedUserGroups.addAll(retrieveUsersGroups(user));
        app.loggedUserFriends.addAll(retrieveUsersFriends(user));
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

        } catch (AuthenticationException e) {
            System.out.println("Invalid credentials!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
