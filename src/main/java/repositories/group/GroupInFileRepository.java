package repositories.group;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import models.User;
import models.group.Group;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupInFileRepository implements IGroupRepository {

    private final Gson gson = new Gson();
    private final Type groupsListType = new TypeToken<List<Group>>() {}.getType();
    private final File groupFile;
    private final List<Group> groups = new ArrayList<>();

    public GroupInFileRepository() {
        groupFile = JsonDatabaseUtil.getFile("groupMessage.json");
        getGroups();
    }

    @Override
    public boolean createGroup(Group group) {
        group.setId(UUID.randomUUID().toString());

        try (var fileWriter = new FileWriter(groupFile)) {
            groups.add(group);
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
        return groups.stream()
                .filter(group -> group.getOwner().equals(user) || group.getUsers().contains(user))
                .collect(Collectors.toList());
    }

    @Override
    public Group getGroup(String id) {
        return groups.stream()
                .filter(group -> group.getId().equals(id))
                .findFirst()
                .get();
    }

    private void getGroups() {
        try (var jsonReader = new JsonReader(new FileReader(groupFile))) {
            List<Group> groupsInFile = this.gson.fromJson(jsonReader, this.groupsListType);
            if (groupsInFile != null) {
                groups.addAll(groupsInFile);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
