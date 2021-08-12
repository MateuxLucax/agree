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

    private int    id;
    private String name;
    private User   owner;

    public Group(String name, User owner) {
        if (owner == null)
            throw new NullPointerException("Groups need an owner");
        this.name = name;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean ownedBy(User user) {
        return owner.equals(user);
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
}
