package models.request;

import models.User;

public class FriendshipRequest extends Request {
    public FriendshipRequest(User from, User to, RequestState state) {
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
