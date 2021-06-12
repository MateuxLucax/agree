package models.group;

import models.User;
import models.message.Message;

import java.util.*;

/* Messages are loaded/unloaded at the top or bottom for pagination.

   To be ordered by date, they need to be "loaded below" if they were sent earlier than
   the currently loaded messages, or "loaded above" if they were sent later than the currently
   loaded messages.

   The point of this is to not load all the messages of the group at once, which could
   be a lot and cause some lag, only a portion of them.
   So if the user scrolls up too much, some messages will be
   unloaded below, and vice-versa. MAX_MESSAGES_LOADED_AT_ONCE is the exact
   limit at which messages will start being unloaded as more are loaded.

   Note that to "unload" is different from "delete":
   the first means it means that the user isn't viewing the message anymore,
   the latter means it's actually deleted from the database

   (I really feel like this stuff about message loading/unloading should be in a separate MessagePage
   class or something like that, and the Group class should actually store no information about messages.
   But maybe it generates a MessagePage by a method or something, idk. )
*/

public class Group {

    private static final int MAX_MESSAGES_LOADED_AT_ONCE = 100;

    private       String id;
    private final String name;
    private final List<User> users;
    private final LinkedList<Message> messages;
    private       Date lastMessageDate;

    public Group(String name) {
        this.name = name;
        this.users = new ArrayList<>();
        this.messages = new LinkedList<>();
    }

    public Group(String name, Date lastMessageDate) {
        this.name = name;
        this.lastMessageDate = lastMessageDate;
        this.users = new ArrayList<>();
        this.messages = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return this.name; }

    // So we can order groups in the user interface to show the ones more active recently
    public void setLastMessageDate(Date date) { this.lastMessageDate = date; }
    public Date getLastMessageDate() { return this.lastMessageDate; }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addUser(User user) { users.add(user); }
    public void removeUser(User user) { users.remove(user); }

    public void loadMessageAbove(Message msg) {
        messages.addFirst(msg);
        if (messages.size() > MAX_MESSAGES_LOADED_AT_ONCE)
            unloadMessageBelow();
    }

    public void loadMessageBelow(Message msg) {
        messages.addLast(msg);
        if (messages.size() > MAX_MESSAGES_LOADED_AT_ONCE)
            unloadMessageAbove();
    }

    public void unloadMessageAbove() { messages.removeFirst(); }
    public void unloadMessageBelow() { messages.removeLast(); }
}
