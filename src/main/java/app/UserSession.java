package app;

import models.User;
import models.group.Group;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;
import repositories.message.IMessageRepository;
import repositories.message.MessageInFileRepository;
import repositories.message.MessageRepositoryTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSession {

    private static UserSession instance;

    private       User user;
    private final List<Group> groups;
    private final List<User> friends;
    private final IGroupRepository groupRepo;
    private final IMessageRepository msgRepo;

    private UserSession() {
        groups = new ArrayList<>();
        friends = new ArrayList<>();

        groupRepo = new GroupInFileRepository();
        msgRepo = new MessageInFileRepository();
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) instance = new UserSession();

        return instance;
    }

    public void initialize() {
        groups.addAll(groupRepo.getGroups(user));

        // TODO
        //       groups.forEach(groupRepo::getUsers)
        //    or groups.forEach(userRepo::getUsers) ?
        for (var group : groups) {
            group.addUser(new User("foo", new Date()));
            group.addUser(new User("bar", new Date()));
            group.addUser(new User("aeiou", new Date()));
        }
        groups.forEach(msgRepo::getMostRecentMessages);

        // TODO
        //     friends.addAll(friendshipRepo.getFriends(user));
        friends.add(new User("john123", new Date()));
        friends.add(new User("asdf", new Date()));
        friends.add(new User("aeiou69", new Date()));
    }

    public User getUser() { return user; }
    public List<Group> getGroups() { return groups; }
    public List<User> getFriends() { return friends; }
    public IMessageRepository getMessageRepository() { return msgRepo; }
    public IGroupRepository getGroupRepository() { return groupRepo; }

    public void setUser(User user) {
        this.user = user;
    }
}
