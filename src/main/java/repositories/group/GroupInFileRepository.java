package repositories.group;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import models.User;
import models.group.Group;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupInFileRepository implements IGroupRepository {

    private final Gson gson = new Gson();
    private final String directory;
    private final String GROUP_FILE = "/groups.json";

    public GroupInFileRepository() {
        this.directory = System.getProperty("user.dir");
    }

    @Override
    public boolean createGroup(Group group) {
        group.setId(UUID.randomUUID().toString());

        File groupFile = new File(this.directory + GROUP_FILE);

        if (!groupFile.exists()) {
            try {
                groupFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (var fileReader = new FileReader(groupFile);
             var jsonReader = new JsonReader(fileReader);
             var fileWriter = new FileWriter(groupFile))
        {
            List<Group> groups = new ArrayList<>();
            List<Group> groupsInFile = this.gson.fromJson(jsonReader, Group[].class);
            if (groupsInFile != null) {
                groups.addAll(groupsInFile);
            }
            groups.add(group);
            fileWriter.write(this.gson.toJson(groups));
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean removeGroup(Group group) {
        return false;
    }

    @Override
    public boolean updateGroup(Group group) {
        return false;
    }

    @Override
    public List<Group> getGroups(User user) {
        return null;
    }

    @Override
    public Group getGroup(int id) {
        return null;
    }
}
