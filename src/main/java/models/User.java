package models;

import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return nickname.equals(user.nickname) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, email);
    }
}
