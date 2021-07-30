package repositories.invite;

import com.google.gson.reflect.TypeToken;
import models.User;
import models.invite.FriendInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import models.invite.InviteState;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InviteRepositoryInFile implements IInviteRepository {

    private final File invitesFile;
    private final List<Invite> invites = new ArrayList<>();

    public InviteRepositoryInFile() {
        // FIXME? Does not work correctly; tries to instance Invite objects, but Invite is abstract
        invitesFile = JsonDatabaseUtil.getFile("invites.json");
        List<Invite> invitesFromFile = JsonDatabaseUtil.readFromFile(invitesFile, new TypeToken<List<Invite>>() {}.getType());
        if (invitesFromFile != null)
            invites.addAll(invitesFromFile);
    }

    @Override
    public List<Invite> getInvites(User user) {
        return invites.stream().filter(invite -> invite.involves(user)).collect(Collectors.toList());
    }

    @Override
    public List<FriendInvite> getFriendInvites(User user) {
        return getInvites(user).stream().filter(i -> i instanceof FriendInvite).map(i -> (FriendInvite) i).collect(Collectors.toList());
    }

    @Override
    public List<GroupInvite> getGroupInvites(User user) {
        return getInvites(user).stream().filter(i -> i instanceof GroupInvite).map(i -> (GroupInvite) i).collect(Collectors.toList());
    }


    @Override
    public List<Invite> getInvites(User user, InviteState state) {
        return invites.stream().filter(invite -> invite.involves(user) && invite.getState().equals(state)).collect(Collectors.toList());
    }

    @Override
    public boolean addInvite(Invite invite) {
        if (invites.contains(invite))
            return false;
        invites.add(invite);
        boolean success = JsonDatabaseUtil.writeToFile(invitesFile, invites);
        if (!success)
            invites.remove(invite);
        return success;
    }

    @Override
    public boolean updateInvite(User user, InviteState state) {
        Optional<Invite> userInvite = invites.stream().filter(invite -> invite.to().equals(user)).findFirst();
        if (userInvite.isPresent()) {
            userInvite.get().setState(state);
            return true;
        }

        return false;
    }
}
