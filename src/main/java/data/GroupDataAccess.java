package data;

import models.User;
import models.group.Group;
import models.message.Message;

import java.util.Date;

public class GroupDataAccess {

    // how many messages to load each time the user scrolls up or down
    private static final int MESSAGES_LOADED = 20;

    private static GroupDataAccess instance;

    public static GroupDataAccess getInstance() {
        if (instance == null) instance = new GroupDataAccess();
        return instance;
    }

    public void populateUsers(Group group) {
        group.addUser(new User("aeiou", new Date()));
        group.addUser(new User("foobar", new Date()));
        group.addUser(new User("!@#$%¨&*", new Date()));
    }

    public void loadMessagesBefore(Group group) {
        // DB queries for messages that were sent before
        // the first currently loaded message (group.getMessages().getFirst())
        // they must be queried in reverse order -- more recent first --
        // so adding at the top preserves order
        User someUser = group.getUsers().get(0);
        for (int i = 0; i < MESSAGES_LOADED; i++)
            group.loadMessageAbove(new Message(someUser, "["+i+"] hello", new Date()));
    }

    public void loadMessagesAfter(Group group, Date date) {
        // DB queries for messages that were sent after
        // the last currently loaded message (group.getMessages().getLast())
        User someUser = group.getUsers().get(0);
        for (int i = 0; i < MESSAGES_LOADED; i++)
            group.loadMessageBelow(new Message(someUser, "["+i+"] goodbye", new Date()));
    }

    public void loadMostRecentMessages(Group group) {
        User someUser = group.getUsers().get(0);
        for (int i = 0; i < MESSAGES_LOADED; i++)
            group.loadMessageBelow(new Message(someUser, "["+i+"] :^)", new Date()));
    }

}
