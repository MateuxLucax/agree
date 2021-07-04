package repositories.friendship;

import com.google.gson.reflect.TypeToken;
import models.User;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FriendshipInFileRepository implements IFriendshipRepository {

    private final File friendshipFile;
    private final List<UserFriendship> friendships = new ArrayList<>();

    public FriendshipInFileRepository() {
        friendshipFile = JsonDatabaseUtil.getFile("friendship.json");
        List<UserFriendship> friendshipsFromFile = JsonDatabaseUtil.readFromFile(friendshipFile, new TypeToken<List<UserFriendship>>() {}.getType());
        if (friendshipsFromFile != null)
            friendships.addAll(friendshipsFromFile);
    }

    private Optional<UserFriendship> getUser(User user) {
        return friendships.stream().filter(userFriendship -> userFriendship.user.equals(user)).findFirst();
    }

    @Override
    public List<User> getFriends(User user) {
        var optionalUser = getUser(user);
        if (optionalUser.isPresent())
            return optionalUser.get().getFriends();
        return new ArrayList<>();
    }

    @Override
    public boolean addFriend(User friend1, User friend2) {
        var optionalUser = getUser(friend1);
        if (optionalUser.isPresent()) {
            var friends = optionalUser.get().getFriends();
            friends.add(friend2);
            friendships.get(friendships.indexOf(optionalUser.get())).setFriends(friends);
            return JsonDatabaseUtil.writeToFile(friendshipFile, friendships);
        }

        return false;
    }

    @Override
    public boolean removeFriend(User friend1, User friend2) {
        var optionalUser = getUser(friend1);
        if (optionalUser.isPresent()) {
            var friends = optionalUser.get().getFriends();
            friends.remove(friend2);
            friendships.get(friendships.indexOf(optionalUser.get())).setFriends(friends);
            return JsonDatabaseUtil.writeToFile(friendshipFile, friendships);
        }

        return false;
    }

    private static class UserFriendship {
        private User user;
        private List<User> friends;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<User> getFriends() {
            return friends;
        }

        public void setFriends(List<User> friends) {
            this.friends = friends;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserFriendship that = (UserFriendship) o;
            return Objects.equals(user, that.user) && Objects.equals(friends, that.friends);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, friends);
        }
    }
}
