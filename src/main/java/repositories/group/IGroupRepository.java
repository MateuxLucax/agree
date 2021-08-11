package repositories.group;

import models.User;
import models.group.Group;

import java.util.List;

public interface IGroupRepository {

    boolean createGroup(Group group);

    boolean deleteGroup(int id);

    boolean renameGroup(Group group);

    List<Group> getGroups(User user);

    List<User> getMembers(Group group);

    boolean changeOwner(Group group, User newOwner);

    boolean removeMember(Group group, User member);
}
