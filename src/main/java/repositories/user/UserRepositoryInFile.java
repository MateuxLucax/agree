package repositories.user;

import com.google.gson.reflect.TypeToken;
import models.User;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepositoryInFile implements IUserRepository {

    private final File userFile;
    private final List<User> users = new ArrayList<>();

    public UserRepositoryInFile() {
        userFile = JsonDatabaseUtil.getFile("users.json");
        List<User> usersFromFile = JsonDatabaseUtil.readFromFile(userFile, new TypeToken<List<User>>() {}.getType());
        if (usersFromFile != null)
            users.addAll(usersFromFile);
    }

    @Override
    public boolean storeUser(User user) {
        users.add(user);
        return JsonDatabaseUtil.writeToFile(userFile, users);
    }

    @Override
    public User getUser(String username, String password) {
        for (var user : users)
            if(user.getNickname().equals(username) && user.getPassword().equals(password)) return user;

        return null;
    }

    @Override
    public List<User> searchUser(String search) {
        return users.stream()
                .filter(u -> u.getNickname().contains(search))
                .collect(Collectors.toList());
    }

    @Override
    public boolean userExists(String username) {
        return users.stream().anyMatch(user -> user.getNickname().equals(username));
    }
}
