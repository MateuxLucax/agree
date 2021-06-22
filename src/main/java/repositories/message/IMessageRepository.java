package repositories.message;

import models.group.Group;
import models.message.Message;

import java.util.Date;

public interface IMessageRepository {

    boolean addMessage(Group group, Message message);

    boolean getMessagesBefore(Group group, Date date);

    boolean getMessagesAfter(Group group, Date date);

    boolean searchMessages(Group group, String search);

    boolean getMostRecentMessages(Group group);

    boolean removeMessage(Group group, Message message);

}
