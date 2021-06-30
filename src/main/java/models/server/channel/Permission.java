package models.server.channel;

// It's a hierarchy: DELETE can also WRITE, WRITE can also READ, READ is sad
public enum Permission {
    NONE,
    READ,
    WRITE,
    DELETE;

    public static boolean canRead(Permission permission) {
        // return permission == READ || permission == WRITE || permission == DELETE;
        return permission != NONE;
    }

    public static boolean canWrite(Permission permission) {
        return permission == WRITE || permission == DELETE;
    }

    public static boolean canDelete(Permission permission) {
        return permission == DELETE;
    }
}
