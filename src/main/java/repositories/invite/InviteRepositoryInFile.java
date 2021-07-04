package repositories.invite;

import com.google.gson.reflect.TypeToken;
import models.User;
import models.invite.Invite;
import models.invite.InviteState;
import utils.JsonDatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InviteRepositoryInFile implements IInviteRepository {

    private final File invitesFile;
    private final List<Invite> invites = new ArrayList<>();

    public InviteRepositoryInFile() {
        invitesFile = JsonDatabaseUtil.getFile("invites.json");
        List<Invite> invitesFromFile = JsonDatabaseUtil.readFromFile(invitesFile, new TypeToken<List<Invite>>() {}.getType());
        if (invitesFromFile != null)
            invites.addAll(invitesFromFile);
    }

    @Override
    public List<Invite> getInvites(User user, InviteState state) {
        return invites.stream().filter(invite -> invite.to().equals(user) && invite.getState().equals(state)).collect(Collectors.toList());
    }

    @Override
    public boolean addInvite(Invite invite) {
        invites.add(invite);
        return JsonDatabaseUtil.writeToFile(invitesFile, invites);
    }

    @Override
    public boolean updateInvite(User user, InviteState state) {
        var userInvite = invites.stream().filter(invite -> invite.to().equals(user)).findFirst();
        if (userInvite.isPresent()) {
            userInvite.get().setState(state);
            return true;
        }

        return false;
    }
}
