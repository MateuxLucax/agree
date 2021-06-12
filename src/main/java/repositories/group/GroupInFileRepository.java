package repositories.group;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import models.User;
import models.group.Group;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupInFileRepository implements IGroupRepository {

    private final Gson gson = new Gson();
    private final String directory;
    private final String GROUP_FILE = "/groups.json";
    private final Type groupsListType = new TypeToken<List<Group>>() {}.getType();

    public GroupInFileRepository() {
        this.directory = System.getProperty("user.dir");
        File groupFile = new File(this.directory + GROUP_FILE);
        if (!groupFile.exists()) {
            try {
                groupFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean createGroup(Group group) {
        group.setId(UUID.randomUUID().toString());
        List<Group> groups = new ArrayList<>();

        try (var jsonReader = new JsonReader(new FileReader(this.directory + GROUP_FILE))) {
            List<Group> groupsInFile = this.gson.fromJson(jsonReader, this.groupsListType);
            if (groupsInFile != null) {
                groups.addAll(groupsInFile);
            }
            groups.add(group);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try (var fileWriter = new FileWriter(this.directory + GROUP_FILE)) {
            fileWriter.write(this.gson.toJson(groups));
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean removeGroup(String id) {
        return false;
    }

    @Override
    public boolean updateGroup(Group group) {
        return false;
    }

    @Override
    public List<Group> getGroups(User user) {
        try (var jsonReader = new JsonReader(new FileReader(this.directory + GROUP_FILE))) {
            List<Group> groups = this.gson.fromJson(jsonReader, this.groupsListType);
            return groups.stream().filter(group -> group.getUsers().contains(user)).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public Group getGroup(String id) {
        try (var jsonReader = new JsonReader(new FileReader(this.directory + GROUP_FILE))) {
            List<Group> groups = this.gson.fromJson(jsonReader, this.groupsListType);
            return groups.stream().filter(group -> group.getId().equals(id)).collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
