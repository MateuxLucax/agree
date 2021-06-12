package services.login;

import exceptions.UnauthorizedUserException;
import models.User;

public interface ILoginService {

    User authenticate(String user, String password) throws UnauthorizedUserException;

    boolean storeUser(User user);

    boolean createUser(User user);
}
