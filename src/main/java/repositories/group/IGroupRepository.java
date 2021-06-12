package repositories.group;

import models.User;
import models.group.Group;

import java.util.List;

public interface IGroupRepository {

    boolean createGroup(Group group);

    boolean removeGroup(String id);

    boolean updateGroup(Group group);

    List<Group> getGroups(User user);

    Group getGroup(String id);
}
