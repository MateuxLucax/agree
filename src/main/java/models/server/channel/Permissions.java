package models.server.channel;

// It's a hierarchy: DELETE can also WRITE, WRITE can also READ, READ is sad
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
