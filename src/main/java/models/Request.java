package models;

public abstract class Request {
    protected final User from;
    protected final User to;
    protected       RequestState state;

    public Request(User from, User to, RequestState state) {
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

    public RequestState getState() {
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public abstract String getText();

    public abstract String getIcon();
    // Shows the requester's icon in FriendshipRequest,
    // Shows the group's icon in GroupInvite

    // With this abstract class and these abstract method
    // we'll be able to use Request polymorphically when
    // showing it to the user
}
