package data;

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

public class UserDataAccess {

    // For now just some dummy implementations until we have an actual database

    private static UserDataAccess instance;
    private final ILoginService loginService;
    private User user;

    public UserDataAccess() {
        this.loginService = new LoginService();
    }

    public static UserDataAccess getInstance() {
        if (instance == null) instance = new UserDataAccess();
        return instance;
    }

    public void authenticate(String name, String password) throws UnauthorizedUserException {
        this.user = loginService.authenticate(name, password);
    }

    public void validateNewAccount(String name, String password)
    throws NameAlreadyInUseException,
           UnsafePasswordException
    {
        if (name.equals("admin"))
            throw new NameAlreadyInUseException("admin");
        if (!this.isPasswordSafe(password))
            throw new UnsafePasswordException();
    }

    public void createAccount(String name, String password) {
        User user = new User(name, new Date());
        user.setPassword(password);
        this.loginService.createUser(user);
    }

    private boolean isPasswordSafe(String password) {
        //return password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}");
        //Just to make it easier for us when we're testing
        return password.length() > 8;
    }

    public User retrieveUser() {
        return this.user;
    }

    public List<Group> retrieveGroups(User user) {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group("group 1", new Date()));
        groups.add(new Group("group 2", new Date()));
        groups.add(new Group("last group", new Date()));
        return groups;
    }

    public List<User> retrieveFriends(User user) {
        List<User> friends = new ArrayList<User>();
        friends.add(new User("john123", new Date()));
        friends.add(new User("asdf", new Date()));
        friends.add(new User("aeiou69", new Date()));
        return friends;
    }
}
