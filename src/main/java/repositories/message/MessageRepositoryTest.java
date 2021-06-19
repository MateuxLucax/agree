package repositories.message;

import models.User;
import models.message.Message;
import models.group.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class MessageRepositoryTest implements IMessageRepository
{
    private static final int LOADED_MESSAGES = 20;

    public boolean addMessage(Group group, Message message) {
        return false;
    }

    public boolean getMessagesBefore(Group group, Date date) {
        User someUser = group.getCreator();
        for (int i = 0; i < LOADED_MESSAGES; i++)
            group.loadMessageAbove(new Message(someUser, "above ["+i+"] :^)", new Date()));
        return true;
    }

    public boolean getMessagesAfter(Group group, Date date) {
        User someUser = group.getCreator();
        for (int i = 0; i < LOADED_MESSAGES; i++)
            group.loadMessageBelow(new Message(someUser, "below ["+i+"] :^)", new Date()));
        return true;
    }

    public boolean getMostRecentMessages(Group group) {
        User someUser = group.getCreator();
        for (int i = 0; i < LOADED_MESSAGES; i++)
            group.loadMessageBelow(new Message(someUser, "hello :^)", new Date()));
        return true;
    }

    public boolean searchMessages(Group group, String search) {
        return false;
    }

    public boolean removeMessage(Group group, Message message) {
        return false;
    }
}
