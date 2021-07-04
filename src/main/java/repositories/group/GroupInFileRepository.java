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
    public boolean updateGroup(Group group) {
        // Nothing guarantees the `group' Group instance we're given is the same in memory
        // as the one with the same id in this repository's `groups' list,
        // so we need to take care of that:
        int i = groups.indexOf(group);  // indexOf uses Group::equals, which compares id
        if (i == -1)
            return false;
        // Make the `groups' list point to the right instance,
        // but keep track of the previous instance in case the update fails
        Group old = groups.get(i);
        groups.set(i, group);
        boolean success = JsonDatabaseUtil.writeToFile(groupFile, groups);
        // There's a problem with this writeToFile:
        // if other Group instances have been changed but not updated
        // on the database, here we'll will update them too, even though
        // this method looks like it should just update one group.
        // I don't think this will be a problem in practice, but the
        // static behaviour of this method is just wrong.
        if (!success)
            groups.set(i, old);
        return success;
    }

    @Override
    public List<Group> getGroups(User user) {
        return groups.stream()
                .filter(g -> g.isMember(user))
                .collect(Collectors.toList());
    }

    @Override
    public Group getGroup(String id) {
        for (var group : groups)
            if (group.getId().equals(id)) return group;
        return null;
    }
}
