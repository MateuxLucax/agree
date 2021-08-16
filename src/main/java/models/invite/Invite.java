package models.invite;

import models.User;

public abstract class Invite {
    protected final User from;
    protected final User to;

    public Invite(User from, User to) {
        this.from  = from;
        this.to    = to;
    }

    public User from() {
        return this.from;
    }

    public boolean from(User u) {
        return this.from.equals(u);
    }

    public User to() {
        return this.to;
    }

    public boolean to(User u) {
        return this.to.equals(u);
    }

    public boolean involves(User user) {
        return from(user) || to(user);
    }

    public String toString() {
        return String.format("Invite from %s to %s", from, to);
    }

    // "Group" or "Friend"; to prefix in "... invite"
    public abstract String kind();

    public abstract String getIcon();
    // Shows the requester's icon in FriendshipRequest,
    // Shows the group's icon in GroupInvite

    public abstract String getText();
}
