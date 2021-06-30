package models.group;

import java.util.Comparator;

public class GroupsSortByName implements Comparator<Group> {
    @Override
    public int compare(Group a, Group b) {
        return b.getName().compareTo(a.getName());
    }
}
