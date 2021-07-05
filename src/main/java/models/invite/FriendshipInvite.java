package models.invite;

import models.User;

public class FriendshipInvite extends Invite {
    public FriendshipInvite(User from, User to, InviteState state) {
        super(from, to, state);
    }

    public String getText() {
        return String.format("%s wants to be friends with %s",
            from.getNickname(), to.getNickname());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipInvite that = (FriendshipInvite) o;
        return to.equals(that.to)
            && from.equals(that.from);
    }

    public String getIcon() {
        // TODO
        return "";
    }
}
