package models;

import models.group.Group;

import java.util.*;

// Maps users to the groups they belong to

public class UserToGroupsMap
{
    private final Map<User, List<Group>> map;

    public UserToGroupsMap() {
        map = new HashMap<>();
    }

    public void add(User u, Group g) {
        if (map.containsKey(u)) {
            map.get(u).add(g);
        } else {
            List<Group> l = new ArrayList<>();
            l.add(g);
            map.put(u, l);
        }
    }

    public List<Group> get(User u) {
        return map.getOrDefault(u, Collections.emptyList());
    }

    public Set<User> userSet() {
        return map.keySet();
    }
}
