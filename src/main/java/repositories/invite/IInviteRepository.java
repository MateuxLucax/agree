package repositories.invite;

import models.User;
import models.invite.FriendInvite;
import models.invite.GroupInvite;
import models.invite.Invite;

import java.util.List;

public interface IInviteRepository {

    List<Invite> getInvites(User user);

    List<FriendInvite> getFriendInvites(User user);

    List<GroupInvite> getGroupInvites(User user);

    boolean addInvite(Invite invite);

    boolean removeInvite(Invite invite);

    /**
     * acceptInvite will not only delete the invite from the database,
     * but also perform related actions -- add a member to the group
     * if a GroupInvite was accepted, add a friendship relation if a
     * FriendInvite was accepted.
     */
    boolean acceptInvite(Invite invite);
}
