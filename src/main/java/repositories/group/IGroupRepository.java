package repositories.group;

import models.User;
import models.group.Group;

import java.util.List;

public interface IGroupRepository {

    boolean createGroup(Group group);

    boolean removeGroup(String id);

    boolean updateGroup(Group group);

    List<Group> getGroups(User user);

    // TODO remove getGroup(id), not used anywhere
    Group getGroup(String id);

    List<User> getMembers(Group group);

    boolean addMember(Group group, User member);

    boolean removeMember(Group group, User member);
}
