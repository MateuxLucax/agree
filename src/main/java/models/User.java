package models;

import java.util.Objects;

public class User {

    private final String nickname;
    private String password;
    private String picture;

    public User(String nickname, String password, String picture) {
        this.nickname = nickname;
        this.password = password;
        this.picture  = picture;
    }

    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public User(String nickname) {
        this(nickname, "", "");
    }

    public String toString() {
        return "User("+nickname+")";
    }

    public String getNickname() { return nickname; }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return nickname.equals(user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}
