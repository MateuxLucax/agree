package repositories.group;

import com.google.gson.reflect.TypeToken;
import models.User;
import models.group.Group;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupInFileRepository implements IGroupRepository {

    private final File groupFile;
    private final List<Group> groups = new ArrayList<>();

    public GroupInFileRepository() {
        groupFile = JsonDatabaseUtil.getFile("groups.json");
        List<Group> groupsFromFile = JsonDatabaseUtil.readFromFile(groupFile, new TypeToken<List<Group>>() {}.getType());
        if (groupsFromFile != null)
            groups.addAll(groupsFromFile);
    }

    @Override
    public boolean createGroup(Group group) {
        group.setId(UUID.randomUUID().toString());
        groups.add(group);
        return JsonDatabaseUtil.writeToFile(groupFile, groups);
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
        for (var group : groups)
            if (group.getId().equals(id)) return group;
        return null;
    }
}
