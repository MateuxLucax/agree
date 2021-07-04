package app;

import models.User;
import models.group.Group;
import models.group.GroupsSortByRecentMessages;
import models.invite.FriendshipInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import models.invite.InviteState;
import repositories.friendship.FriendshipInFileRepository;
import repositories.friendship.IFriendshipRepository;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;
import repositories.invite.IInviteRepository;
import repositories.invite.InviteRepositoryInFile;
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
    private final List<Invite> invites;

    private final IGroupRepository groupRepo;
    private final IMessageRepository msgRepo;
    private final IUserRepository userRepo;
    private final IFriendshipRepository friendshipRepository;
    private final IInviteRepository inviteRepository;

    private UserSession() {
        groups = new ArrayList<>();
        friends = new ArrayList<>();
        invites = new ArrayList<>();

        groupRepo = new GroupInFileRepository();
        msgRepo = new MessageInFileRepository();
        userRepo = new UserRepositoryInFile();
        friendshipRepository = new FriendshipInFileRepository();
        inviteRepository = new InviteRepositoryInFile();
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void initialize(User user) {
        this.user = user;
        groups.addAll(groupRepo.getGroups(user));  // Already retrieves the users
        groups.forEach(msgRepo::getMostRecentMessages);
        friends.addAll(friendshipRepository.getFriends(user));
        invites.addAll(inviteRepository.getInvites(user));
//        generateDummyData(user);
    }

    private void generateDummyData(User user) {
        // Dummy data for testing, while we don't yet persist all of our data
        User us1 = new User("joao", new Date());
        User us2 = new User("jose", new Date());
        User us3 = new User("mario", new Date());
        Group g1 = new Group("test group :^)", user);
        groups.add(g1);
        g1.addUser(us1);
        g1.addUser(us2);
        Group g2 = new Group("yet another group", us1);
        groups.add(g2);
        g2.addUser(user);
        g2.addUser(us2);
        g2.addUser(us3);
        friends.add(us3);
        Invite req1 = new FriendshipInvite(user, us1, InviteState.PENDING);
        Invite req2 = new FriendshipInvite(us2, user, InviteState.PENDING);
        Invite req3 = new GroupInvite(user, us3, InviteState.PENDING, groups.get(0));
        invites.add(req1);
        invites.add(req2);
        invites.add(req3);
        Group otherGroup = new Group(":o", us3);
        Invite req4 = new GroupInvite(us3, user, InviteState.PENDING, otherGroup);
        invites.add(req4);
    }

    public User getUser() { return user; }
    public List<Group> getGroups() {
        groups.sort(new GroupsSortByRecentMessages());
        return groups;
    }
    public List<User> getFriends() { return friends; }
    public List<Invite> getInvites() { return invites; }

    public IMessageRepository getMessageRepository() { return msgRepo; }
    public IGroupRepository getGroupRepository() { return groupRepo; }
    public IUserRepository getUserRepository() { return userRepo; }
    public IFriendshipRepository getFriendshipRepository() { return friendshipRepository; }
    public IInviteRepository getInviteRepository() {
        return inviteRepository;
    }
}
