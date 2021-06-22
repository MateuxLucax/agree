package models.message;

import models.User;

import java.util.Date;
import java.util.Objects;

public class Message implements Comparable<Message> {

    /*
     * Message initially had a Member instead of a User attribute, which makes sense in the context of Servers.
     * But Message now has an User instead of Member attribute because Groups have only Users in them, not Members.
     * The problem is that if Message had a Member attribute then Groups, which have no 'members', wouldn't be able to have messages...
     * This change might end up not being a problem at all. We'll see.
     */
    private final User user;
    private final String text;
    private final Date sentAt;

    public Message(User user, String text, Date sentAt) {
        this.user = user;
        this.text = text;
        this.sentAt = sentAt;
    }

    public User getUser() { return user; }
    public Date sentAt() { return sentAt; }
    public String getText() { return text; }

    public int compareTo(Message other) {
        return sentAt.compareTo(other.sentAt);
    }

    public String toString() {
        return String.format("Message{ %s (sent by %s at %s) }", text, user.getNickname(), sentAt.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(user, message.user) && Objects.equals(text, message.text) && Objects.equals(sentAt, message.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, text, sentAt);
    }
}
