package models.server.channel;

// DELETE can WRITE, WRITE can READ, READ is sad
public enum Permissions {
    READ,
    WRITE,
    DELETE;

    public static boolean canRead(Permissions permission) {
        // return permission == READ || permission == WRITE || permission == DELETE;
        return true;
    }

    public static boolean canWrite(Permissions permission) {
        return permission == WRITE || permission == DELETE;
    }

    public static boolean canDelete(Permissions permission) {
        return permission == DELETE;
    }
}
