package app;

import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import models.User;
import models.group.Group;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;
import repositories.message.IMessageRepository;
import repositories.message.MessageRepositoryTest;
import services.login.ILoginService;
import services.login.LoginService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSession {

    private       User user;
    private final List<Group> groups;
    private final List<User> friends;
    private final ILoginService loginService;
    private final IGroupRepository groupRepo;
    private final IMessageRepository msgRepo;

    private UserSession() {
        groups = new ArrayList<>();
        friends = new ArrayList<>();

        loginService = new LoginService();
        groupRepo = new GroupInFileRepository();
        msgRepo = new MessageRepositoryTest();
    }

    public static UserSession createAccount(String name, String password)
    throws UnsafePasswordException,
           NameAlreadyInUseException
    {
        if (name.equals("admin")) {
            throw new NameAlreadyInUseException();
        }
        // boolean passwordIsSafe = password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{8,}");
        boolean passwordIsSafe = password.length() > 8;
        if (!passwordIsSafe) {
            throw new UnsafePasswordException();
        }

        var session = new UserSession();
        session.user = new User(name, new Date());
        session.user.setPassword(password);
        session.loginService.createUser(session.user);
        session.initialize();
        return session;
    }

    public static UserSession authenticate(String name, String password)
    throws UnauthorizedUserException
    {
        var session = new UserSession();
        session.user = session.loginService.authenticate(name, password);
        session.initialize();
        return session;
    }

    private void initialize() {
        // Ideally:

        // groups.addAll(groupRepo.getGroups(user));
        // groups.forEach(groupRepo::getUsers);
        // groups.forEach(msgRepo::getMostRecentMessages);
        // friends.addAll(friendshipRepo.getFriends(user));

        // For now:

        groups.add(new Group("group 1"));
        groups.add(new Group("group 2"));
        groups.add(new Group("last group"));

        for (var group : groups) {
            group.addUser(new User("foo", new Date()));
            group.addUser(new User("bar", new Date()));
            group.addUser(new User("aeiou", new Date()));
        }
        groups.forEach(msgRepo::getMostRecentMessages);

        friends.add(new User("john123", new Date()));
        friends.add(new User("asdf", new Date()));
        friends.add(new User("aeiou69", new Date()));

    }

    public User getUser() { return user; }
    public List<Group> getGroups() { return groups; }
    public List<User> getFriends() { return friends; }
    public IMessageRepository getMessageRepository() { return msgRepo; }
    public IGroupRepository getGroupRepository() { return groupRepo; }

}
