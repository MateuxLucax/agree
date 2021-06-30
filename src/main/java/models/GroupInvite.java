package models;

import models.group.Group;

public class GroupInvite extends Request {
    private Group group;

    public GroupInvite(User from, User to, RequestState state, Group group) {
        super(from, to, state);
        this.group = group;
    }

    public String getText() {
        return String.format("%s invites %s to join %s",
            from.getNickname(), to.getNickname(), group.getName());
    }

    public String getIcon() {
        // TODO
        return "<group.picture()>";
    }
}
