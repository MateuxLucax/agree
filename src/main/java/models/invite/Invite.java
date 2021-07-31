package models.invite;

import models.User;

public abstract class Invite {
    protected final User from;
    protected final User to;
    protected InviteState state;

    public Invite(User from, User to, InviteState state) {
        this.from  = from;
        this.to    = to;
        this.state = state;
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

    public InviteState getState() {
        return state;
    }

    public void setState(InviteState state) {
        this.state = state;
    }

    public boolean isState(InviteState s) {
        return this.state.equals(s);
    }

    public String toString() {
        return String.format("%s invite from %s to %s", state, from, to);
    }

    public abstract String getIcon();
    // Shows the requester's icon in FriendshipRequest,
    // Shows the group's icon in GroupInvite

    public abstract String getText();
}
