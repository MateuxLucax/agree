package models.group;

import models.message.Message;

import java.util.Comparator;
import java.util.LinkedList;

public class GroupsSortByRecentMessages implements Comparator<Group> {
    @Override
    public int compare(Group a, Group b) {
        LinkedList<Message> ams = a.getMessages();
        LinkedList<Message> bms = b.getMessages();
        boolean aemp = ams.isEmpty();
        boolean bemp = bms.isEmpty();
        if (aemp && bemp) return  0;  // [] == []
        if (bemp)         return  1;  // a  >  []
        if (aemp)         return -1;  // [] <  b
        return ams.getLast().compareTo(bms.getLast());
        // most recent message = last item in the list
        // because new messages are appended at the end
    }
}
