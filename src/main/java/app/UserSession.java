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
        // For now some dummy data to test
        User us1 = new User("john123", new Date());
        User us2 = new User("asdf", new Date());
        User us3 = new User("aeiou69", new Date());
        friends.add(us1);
        friends.add(us2);
        friends.add(us3);
        for (int i = 0; i < 40; i++) {
            friends.add(new User(":^)", new Date()));
            // FIXME this shows that we can't scroll tabs...
            //     if we have too many friends it'll just start stacking the tabs on the left.
            //     Unless we can scroll through the tabs instead of listing all of them,
            //     we *can't* use tabs for this
        }

        // TODO requests.addAll(requestRepo.getRequests(user));
        // For now some dummy data to test
        requests.add(new FriendshipRequest(user, us1, RequestState.PENDING));
        requests.add(new FriendshipRequest(us2, user, RequestState.PENDING));
        requests.add(new FriendshipRequest(us3, user, RequestState.ACCEPTED));
        if (!groups.isEmpty()) {
            requests.add(new GroupInvite(user, us1, RequestState.DECLINED, groups.get(0)));
            requests.add(new GroupInvite(us3, user, RequestState.ACCEPTED, groups.get(0)));
        }
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
