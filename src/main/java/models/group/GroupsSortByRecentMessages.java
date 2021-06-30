package models.group;

import java.util.Comparator;

public class GroupsSortByRecentMessages implements Comparator<Group> {
    @Override
    public int compare(Group a, Group b) {
        return b.getMessages().getFirst().compareTo(a.getMessages().getFirst());
    }
}
