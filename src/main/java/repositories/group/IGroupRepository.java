package repositories.group;

import models.User;
import models.group.Group;

import java.util.List;

public interface IGroupRepository {

    boolean createGroup(Group group);

    boolean removeGroup(Group group);

    boolean updateGroup(Group group);

    List<Group> getGroups(User user);

    Group getGroup(int id);
}
