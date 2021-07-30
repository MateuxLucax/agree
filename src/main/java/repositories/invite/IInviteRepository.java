package repositories.invite;

import models.User;
import models.invite.FriendInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import models.invite.InviteState;

import java.util.List;

public interface IInviteRepository {

    List<Invite> getInvites(User user);

    List<FriendInvite> getFriendInvites(User user);

    List<GroupInvite> getGroupInvites(User user);

    List<Invite> getInvites(User user, InviteState state);

    boolean addInvite(Invite invite);

    boolean updateInvite(User user, InviteState state);
}
