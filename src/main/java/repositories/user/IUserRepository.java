package repositories.user;

import models.User;

public interface IUserRepository {

    boolean storeUser(User user);

    User getUser(String username, String password);
}
