package controllers.group;

import controllers.AbstractChatController;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageRepository;

import java.util.Date;
import java.util.List;


public class GroupChatController extends AbstractChatController {

    private final User user;
    private final Group group;
    private final IMessageRepository msgRepo;

    public GroupChatController(User user, Group group) {
        super(group.getName() + " (group): chat");
        this.user    = user;
        this.group   = group;
        this.msgRepo = new MessageRepository();
        initialise();
        // Why a separate initialise() method? Isn't the constructor supposed to initialise the object?
        // It's because initialise() includes a call to getNewestMessages,
        // which relies on the msgRepo variable being initialised.
        // This means that we have to initialise that variable before calling initialise().
        // We can't do what initialise does in the constructor because the super() call
        // must be the first one -- this.msgRepo = new MessageRepository() can't come above it.
    }

    @Override
    protected List<Message> getNewestMessages(int numberOfMessages) {
        return msgRepo.getNewestMessages(group, numberOfMessages);
    }

    @Override
    protected List<Message> getMessagesBefore(Date date, int numberOfMessages) {
        return msgRepo.getMessagesBefore(group, date, numberOfMessages);
    }

    @Override
    protected List<Message> getMessagesAfter(Date date) {
        return msgRepo.getMessagesAfter(group, date);
    }

    @Override
    protected boolean addMessage(String text) {
        var msg = new Message(user, text, new Date());
        return msgRepo.addMessage(group, msg);
    }

    @Override
    protected boolean removeMessage(Message msg) {
        return msgRepo.removeGroupMessage(msg);
    }

    @Override
    protected boolean canUserDeleteThisMessage(Message msg) {
        return group.ownedBy(user) || msg.getUser().equals(user);
    }
}
