package models.request;

import models.User;
import models.group.Group;

public class GroupInvite extends Request {
    private Group group;

    public GroupInvite(User from, User to, RequestState state, Group group) {
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