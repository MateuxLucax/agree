package repositories.invite;

import models.User;
import models.invite.FriendshipInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import models.invite.InviteState;

import java.util.List;

public interface IInviteRepository {

    List<Invite> getInvites(User user);
    List<Invite> getInvites(User user, InviteState state);

    List<FriendshipInvite> getFriendInvites(User user);
    List<FriendshipInvite> getFriendInvites(User user, InviteState state);

    List<GroupInvite> getGroupInvites(User user);
    List<GroupInvite> getGroupInvites(User user, InviteState state);

    boolean addInvite(Invite invite);

    boolean updateInvite(Invite invite);
}
