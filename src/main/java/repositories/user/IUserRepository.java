package repositories.user;

import models.User;

import java.util.List;

public interface IUserRepository {

    boolean createUser(User user);

    User getUser(String username, String password);

    List<User> searchUsers(String search);

    boolean userExists(String username);

    boolean updateUser(User user);
}
