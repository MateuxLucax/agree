package models;

import java.util.Date;

public class User {

    private final String nickname;

    private final Date createdAt;

    private String email;

    // private String picture;

    public User(String nickname, Date createdAt) {
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public String getNickname() { return nickname; }

    public Date getCreationDate() { return createdAt; }
}
