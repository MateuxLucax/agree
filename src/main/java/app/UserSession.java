package app;

import models.User;
import models.group.Group;
import models.group.GroupsSortByRecentMessages;
import models.invite.Invite;
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
    }

    public User getUser() { return user; }
    public List<Group> getGroups() {
        groups.sort(new GroupsSortByRecentMessages());
        return groups;
    }

    public IInviteRepository getInviteRepository() {
        return inviteRepository;
    }
}
