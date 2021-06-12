package repositories.message;

import models.group.Group;
import models.message.Message;

import java.util.Date;
import java.util.List;

public class MessageInFileRepository implements IMessageRepository {

    @Override
    public boolean addMessage(Group group, Message message) {
        return false;
    }

    @Override
    public boolean getMessagesBefore(Group group, Date date) {
        return false;
    }

    @Override
    public boolean getMessagesAfter(Group group, Date date) {
        return false;
    }

    @Override
    public boolean searchMessages(Group group, String search) {
        return false;
    }

    @Override
    public boolean getMostRecentMessages(Group group) {
        return false;
    }

    @Override
    public boolean removeMessage(Group group, Message message) {
        return false;
    }
}
