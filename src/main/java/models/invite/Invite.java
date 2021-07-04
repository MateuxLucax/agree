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

    public User to() {
        return this.to;
    }

    public InviteState getState() {
        return state;
    }

    public void setState(InviteState state) {
        this.state = state;
    }

    public String toString() {
        return String.format("%s request from %s to %s", state, from, to);
    }

    public abstract String getIcon();
    // Shows the requester's icon in FriendshipRequest,
    // Shows the group's icon in GroupInvite

    public abstract String getText();

    // With this abstract class and these abstract method
    // we'll be able to use Request polymorphically when
    // showing it to the user
}
