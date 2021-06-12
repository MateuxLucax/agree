package repositories.friendship;

import models.User;

import java.util.List;

public class FriendshipInFileRepository implements IFriendshipRepository {
    @Override
    public List<User> getFriends(User user) {
        return null;
    }

    @Override
    public boolean addFriend(User friend1, User friend2) {
        return false;
    }

    @Override
    public boolean removeFriend(User friend1, User friend2) {
        return false;
    }
}
