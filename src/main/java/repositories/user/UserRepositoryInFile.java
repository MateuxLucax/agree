package repositories.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import models.User;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepositoryInFile implements IUserRepository {

    private final Gson gson = new Gson();
    private final File userFile;
    private final List<User> users = new ArrayList<>();

    public UserRepositoryInFile() {
        userFile = JsonDatabaseUtil.getFile("users.json");
        getUsersFromJson();
    }

    @Override
    public boolean storeUser(User user) {
        try (var fileWriter = new FileWriter(userFile)) {
            users.add(user);
            fileWriter.write(this.gson.toJson(users));
            getUsersFromJson();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public User getUser(String username, String password) {
        return users.stream()
                .filter(user -> user.getNickname().equals(username) && user.getPassword().equals(password))
                .collect(Collectors.toList())
                .get(0);
    }

    @Override
    public boolean userExists(String username) {
        return users.stream().anyMatch(user -> user.getNickname().equals(username));
    }

    private void getUsersFromJson() {
        try (var jsonReader = new JsonReader(new FileReader(userFile))) {
            List<User> usersInFile = this.gson.fromJson(jsonReader, new TypeToken<List<User>>() {}.getType());
            if (usersInFile != null) {
                users.addAll(usersInFile);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
