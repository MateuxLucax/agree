package models.message;

import models.User;

import java.util.Date;
import java.util.Objects;

public class Message implements Comparable<Message> {

    private       int    id;
    private final User   user;
    private final String text;
    private final Date   sentAt;

    public Message(int id, User user, String text, Date sentAt) {
        this.id     = id;
        this.user   = user;
        this.text   = text;
        this.sentAt = sentAt;
    }

    public Message(User user, String text, Date sentAt) {
        this.user   = user;
        this.text   = text;
        this.sentAt = sentAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId()      { return id; }
    public User getUser()   { return user; }
    public String getText() { return text; }
    public Date sentAt()    { return sentAt; }


    public int compareTo(Message other) {
        return sentAt.compareTo(other.sentAt);
    }

    public String toString() {
        return String.format("Message{ %s (sent by %s at %s) }", text, user.getNickname(), sentAt.toString());
    }
}
