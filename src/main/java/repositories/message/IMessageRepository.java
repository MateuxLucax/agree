package repositories.message;

import models.User;
import models.group.Group;
import models.message.Message;

import java.util.Date;
import java.util.List;

public interface IMessageRepository {

    boolean addMessage(Group group, Message message);

    boolean removeGroupMessage(Message msg);

    List<Message> getNewestMessages(Group group, int numberOfMessages);

    List<Message> getMessagesAfter(Group group, Date date);

    List<Message> getMessagesBefore(Group group, Date date, int numberOfMessages);

    boolean addMessages(User friend1, User friend2, Message message);

    boolean removeFriendMessage(Message msg);

    List<Message> getNewestMessages(User friend1, User friend2, int numberOfMessages);

    List<Message> getMessagesAfter(User friend1, User friend2, Date date);

    List<Message> getMessagesBefore(User friend1, User friend2, Date date, int numberOfMessages);
}
