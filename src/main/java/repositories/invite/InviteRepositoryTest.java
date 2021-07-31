package repositories.invite;

import models.User;
import models.invite.FriendshipInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import models.invite.InviteState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InviteRepositoryTest implements IInviteRepository
{
    private static List<Invite> invites = new ArrayList<>();

    @Override
    public List<Invite> getInvites(User user) {
        return invites;
    }

    @Override
    public List<Invite> getInvites(User user, InviteState state) {
        return invites.stream().filter(i -> i.isState(state)).collect(Collectors.toList());
    }

    @Override
    public List<FriendshipInvite> getFriendInvites(User user) {
        return invites.stream().filter(i -> i instanceof FriendshipInvite).map(i -> (FriendshipInvite) i).collect(Collectors.toList());
    }

    @Override
    public List<FriendshipInvite> getFriendInvites(User user, InviteState state) {
        return getFriendInvites(user).stream().filter(i -> i.isState(state)).collect(Collectors.toList());
    }

    @Override
    public List<GroupInvite> getGroupInvites(User user) {
        return invites.stream().filter(i -> i instanceof GroupInvite).map(i -> (GroupInvite) i).collect(Collectors.toList());
    }

    @Override
    public List<GroupInvite> getGroupInvites(User user, InviteState state) {
        return getGroupInvites(user).stream().filter(i -> i.isState(state)).collect(Collectors.toList());
    }

    @Override
    public boolean addInvite(Invite invite) {
        invites.add(invite);
        return true;
    }

    @Override
    public boolean updateInvite(Invite invite) {
        Invite inv = null;
        for (Invite i : invites)
            if (i.equals(invite)) {
                inv = invite;
                break;
            }
        if (inv == null)
            return false;
        inv.setState(invite.getState());
        return true;
    }
}
