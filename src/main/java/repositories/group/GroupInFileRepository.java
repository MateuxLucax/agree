package repositories.group;

import com.google.gson.reflect.TypeToken;
import models.User;
import models.group.Group;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        for (int i = 0; i < groups.size(); i++) {
            if (id.equals(groups.get(i).getId())) {
                Group removed = groups.remove(i);
                boolean success = JsonDatabaseUtil.writeToFile(groupFile, groups);
                if (!success)
                    groups.add(removed);
                return success;
            }
        }
        return false;
    }

    @Override
    public boolean renameGroup(Group group) {
        return false;
    }

    @Override
    public List<Group> getGroups(User user) {
        return null;
    }

    @Override
    public Group getGroup(String id) {
        for (var group : groups)
            if (group.getId().equals(id)) return group;
        return null;
    }

    @Override
    public List<User> getMembers(Group group) {
        return null;
    }

    @Override
    public boolean changeOwner(Group group, User newOwner) {
        return false;
    }

    @Override
    public boolean removeMember(Group group, User member) {
        return false;
    }
}
