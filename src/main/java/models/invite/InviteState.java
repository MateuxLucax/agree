package models.invite;

public enum InviteState {
    PENDING (0),
    ACCEPTED(1),
    DECLINED(2);

    private static final InviteState[] fromId = { PENDING, ACCEPTED, DECLINED };

    public static InviteState fromId(int id) {
        return fromId[id];
    }

    private final int id;

    InviteState(int id) {
        this.id = id;
    }

    public int id() {
        return this.id;
    }

}
