package models.invite;

import models.User;
import models.group.Group;

public class GroupInvite extends Invite {
    private final Group group;

    public GroupInvite(User from, User to, InviteState state, Group group) {
        super(from, to, state);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public String getText() {
        return String.format("%s invites %s to join %s", from.getNickname(), to.getNickname(), group.getName());
    }

    public String getIcon() {
        // TODO
        return "";
    }
}
