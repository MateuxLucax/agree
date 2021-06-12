package models;

import java.util.Date;

public class User {

    private final String nickname;

    private final Date createdAt;

    private String email;

    private String password;

    // private String picture;

    public User(String nickname, Date createdAt) {
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public String toString() {
        return "User("+nickname+")";
    }

    public String getNickname() { return nickname; }

    public Date getCreationDate() { return createdAt; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
