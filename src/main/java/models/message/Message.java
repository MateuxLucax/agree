package models.message;

import models.User;
import models.server.channel.Member;

import java.util.Date;

public class Message implements Comparable<Message> {

    /*
     * Message initially had a Member instead of a User attribute, which makes sense in the context of Servers.
     * But Message now has an User instead of Member attribute because Groups have only Users in them, not Members.
     * The problem is that if Message had a Member attribute then Groups, which have no 'members', wouldn't be able to have messages...
     * This change might end up not being a problem at all. We'll see.
     */
    private User user;
    private String text;
    private Date sentAt;

    public Message(User user, String text, Date sentAt) {
        this.user = user;
        this.text = text;
        this.sentAt = sentAt;
    }

    public User getUser() { return user; }
    public Date getSentAt() { return sentAt; }
    public String getText() { return text; }

    public int compareTo(Message other) {
        return sentAt.compareTo(other.sentAt);
    }

    public String toString() {
        return String.format("Message{ %s (sent by %s at %s) }", text, user.getNickname(), sentAt.toString());
    }
}
