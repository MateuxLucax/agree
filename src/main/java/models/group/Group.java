package models.group;

import models.User;
import models.message.Message;

import java.util.*;

public class Group {

    private int    id;
    private String name;
    private User   owner;
    private String picture;

    public Group(int id, String name, User owner) {
        if (owner == null) throw new NullPointerException("Groups need an owner");
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public Group(String name, User owner) {
        this(0, name, owner);
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

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
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
