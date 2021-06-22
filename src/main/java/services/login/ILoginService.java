package services.login;

import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import models.User;

public interface ILoginService {

    User authenticate(String user, String password) throws UnauthorizedUserException;

    boolean storeUser(User user);

    boolean createUser(User user) throws NameAlreadyInUseException, UnsafePasswordException;
}
