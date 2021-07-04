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
    private       String name;
    private       User   owner;
    private final List<User> users;
    private final LinkedList<Message> messages;

    public Group(String name, User owner) {
        this.name = name;
        setOwner(owner);
        this.users = new ArrayList<>();
        this.messages = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User newOwner) {
        if (newOwner == null)
            throw new NullPointerException("Groups need an owner");
        owner = newOwner;
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public void addUser(User user) { users.add(user); }

    public void removeUser(User user) {
        if (!user.equals(owner))
            users.remove(user);
    }

    public boolean isMember(User u) {
        return u.equals(owner) || users.contains(u);
    }

    public LinkedList<Message> getMessages() {
        return messages;
    }

    public void loadMessageAbove(Message msg) {
        if (messages.size() + 1 > MAX_MESSAGES_LOADED_AT_ONCE)
            messages.removeLast();
        messages.addFirst(msg);
    }

    public void loadMessageBelow(Message msg) {
        if (messages.size() + 1> MAX_MESSAGES_LOADED_AT_ONCE)
            messages.removeFirst();
        messages.addLast(msg);
    }

    public Date oldestMessageDate() {
        return messages.isEmpty() ? new Date() : messages.getFirst().sentAt();
    }

    public Date newestMessageDate() {
        return messages.isEmpty() ? new Date() : messages.getLast().sentAt();
    }

    public void setMessages(List<Message> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }

    public static Comparator<Group> mostRecentActivityFirst() {
        return new GroupsSortByRecentMessages();
    }
}
