package app;

import models.request.FriendshipRequest;
import models.request.GroupInvite;
import models.request.Request;
import models.User;
import models.group.Group;
import models.group.GroupsSortByRecentMessages;
import models.request.RequestState;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;
import repositories.message.IMessageRepository;
import repositories.message.MessageInFileRepository;
import repositories.user.IUserRepository;
import repositories.user.UserRepositoryInFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSession {

    private static UserSession instance;

    private       User user;
    private final List<Group> groups;
    private final List<User> friends;
    private final List<Request> requests;

    private final IGroupRepository groupRepo;
    private final IMessageRepository msgRepo;
    private final IUserRepository userRepo;

    private UserSession() {
        groups = new ArrayList<>();
        friends = new ArrayList<>();
        requests = new ArrayList<>();

        groupRepo = new GroupInFileRepository();
        msgRepo = new MessageInFileRepository();
        userRepo = new UserRepositoryInFile();
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void initialize(User user) {
        this.user = user;
        groups.addAll(groupRepo.getGroups(user));  // Already retrieves the users
        groups.forEach(msgRepo::getMostRecentMessages);
        // TODO friends.addAll(friendshipRepo.getFriends(user));
        // TODO requests.addAll(requestRepo.getRequests(user));

        // Dummy data for testing, while we don't yet persist all of our data
        groups.add(new Group("testGroup:)", user));
        User us1 = new User("joao", new Date());
        User us2 = new User("jose", new Date());
        User us3 = new User("mario", new Date());
        for (var group : groups) {
            group.addUser(us1);
            group.addUser(us2);
        }
        friends.add(us3);
        Request req1 = new FriendshipRequest(user, us1, RequestState.PENDING);
        Request req2 = new FriendshipRequest(us2, user, RequestState.PENDING);
        Request req3 = new GroupInvite(user, us3, RequestState.PENDING, groups.get(0));
        requests.add(req1);
        requests.add(req2);
        requests.add(req3);
        Group otherGroup = new Group(":o", us3);
        Request req4 = new GroupInvite(us3, user, RequestState.PENDING, otherGroup);
        requests.add(req4);
    }

    public User getUser() { return user; }
    public List<Group> getGroups() {
        groups.sort(new GroupsSortByRecentMessages());
        return groups;
    }
    public List<User> getFriends() { return friends; }
    public List<Request> getRequests() { return requests; }

    public IMessageRepository getMessageRepository() { return msgRepo; }
    public IGroupRepository getGroupRepository() { return groupRepo; }
    public IUserRepository getUserRepository() { return userRepo; }
}
