package repositories.user;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import models.User;

import java.io.FileReader;
import java.io.FileWriter;

public class UserRepositoryInFile implements IUserRepository {

    private final Gson gson = new Gson();
    private final String directory;

    public UserRepositoryInFile() {
        this.directory = System.getProperty("user.dir");
    }

    @Override
    public boolean storeUser(User user) {
        try {
            FileWriter fileWriter = new FileWriter(this.directory + "/user.json");
            fileWriter.write(this.gson.toJson(user));
            fileWriter.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public User getUser(String username, String password) {
        try {
            User user = this.gson.fromJson(new JsonReader(new FileReader(this.directory + "/user.json")), User.class);
            if (user.getNickname().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}