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

    public String getIcon() {
        // TODO
        return "";
    }
}
