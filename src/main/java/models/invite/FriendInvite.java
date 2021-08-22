package models.invite;

import models.User;

public class FriendInvite extends Invite
{
    public FriendInvite(User from, User to) {
        super(from, to);
    }

    public String getText() {
        return String.format("%s wants to be friends with %s",
            from.getNickname(), to.getNickname());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendInvite that = (FriendInvite) o;
        return to.equals(that.to)
            && from.equals(that.from);
    }

    public String kind() { return "Friend"; }

    public String getIcon() {
        return from.getPicture();
    }
}
