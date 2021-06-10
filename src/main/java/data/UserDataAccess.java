package data;

import exceptions.IncorrectPasswordException;
import exceptions.NameAlreadyInUseException;
import exceptions.UnsafePasswordException;
import models.User;
import models.group.Group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDataAccess {

    // For now just some dummy implementations until we have an actual database

    private static UserDataAccess instance;

    public UserDataAccess() { }

    public static UserDataAccess getInstance() {
        if (instance == null) instance = new UserDataAccess();
        return instance;
    }

    public void authenticate(String name, String password)
    throws IncorrectPasswordException
    {
        String thePassword = "123";
        if (!password.equals(thePassword))
            throw new IncorrectPasswordException(name);
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
        // Access DB to create account...
    }

    private boolean isPasswordSafe(String password) {
        return password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}");
    }

    public User retrieveUser(String name) {
        return new User(name, new Date());
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
