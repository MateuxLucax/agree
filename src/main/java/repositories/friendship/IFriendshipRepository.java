package repositories.friendship;

import models.User;

import java.util.List;

public interface IFriendshipRepository {

    List<User> getFriends(User user);

    boolean addFriend(User friend1, User friend2);

    boolean removeFriend(User friend1, User friend2);
}
