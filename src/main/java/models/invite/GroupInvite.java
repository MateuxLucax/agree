package models.invite;

import models.User;
import models.group.Group;

public class GroupInvite extends Invite {
    private final Group group;

    public GroupInvite(User from, User to, Group group) {
        super(from, to);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupInvite that = (GroupInvite) o;
        return to.equals(that.to)
            && from.equals(that.from)
            && group.equals(that.group);
    }
    public String getText() {
        return String.format("%s invites %s to join %s", from.getNickname(), to.getNickname(), group.getName());
    }

    public String kind() { return "Group"; }

    public String getIcon() {
        return group.getPicture();
    }
}
