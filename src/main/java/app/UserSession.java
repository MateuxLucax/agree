package app;

import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import models.User;
import models.group.Group;
import services.login.ILoginService;
import services.login.LoginService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSession {

    private User user;
    private List<Group> groups;
    private List<User> friends;
    private ILoginService loginService;

    private static UserSession instance;

    private UserSession() {
        groups = new ArrayList<>();
        friends = new ArrayList<>();
        loginService = new LoginService();
    }

    public static void createAccount(String name, String password)
    throws UnsafePasswordException,
           NameAlreadyInUseException
    {
        if (instance != null)
            return;
        // boolean passwordIsSafe = password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{8,}");
        boolean passwordIsSafe = password.length() > 8;
        if (!passwordIsSafe)
            throw new UnsafePasswordException();
        if (name.equals("admin"))
            throw new NameAlreadyInUseException();

        var session = new UserSession();
        session.user = new User(name, new Date());
        session.user.setPassword(password);
        session.loginService.createUser(session.user);

        session.loadGroups();
        session.loadFriends();

        instance = session;
    }

    public static void authenticate(String name, String password)
    throws UnauthorizedUserException
    {
        if (instance != null)
            return;

        var session = new UserSession();
        session.user = session.loginService.authenticate(name, password);

        session.loadGroups();
        session.loadFriends();

        instance = session;
    }

    public static UserSession getInstance() {
        if (instance == null)
            throw new NullPointerException("User session not started yet! Call UserSession.createAccount() or authenticate() first.");
        return instance;
    }

    public User getUser() { return user; }
    public List<Group> getGroups() { return groups; }
    public List<User> getFriends() { return friends; }

    public void loadGroups() {
        // Would get user's groups from a DB / repository
        // Now let's just load some placeholder data
        groups.add(new Group("group 1", new Date()));
        groups.add(new Group("group 2", new Date()));
        groups.add(new Group("last group", new Date()));
    }

    public void loadFriends() {
        // Same as loadGroups
        friends.add(new User("john123", new Date()));
        friends.add(new User("asdf", new Date()));
        friends.add(new User("aeiou69", new Date()));
    }

}
