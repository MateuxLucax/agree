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
        super(group.getName() + "");
        this.user    = user;
        this.group   = group;
        this.msgRepo = new MessageRepository();
        initialise();
        // Why a separate initialise() method? Isn't the constructor supposed to initialise the object?
        // The problem is that initialise calls getMostRecentMessages, which requires the msgRepo
        // to be initialised (otherwise a NullPointerException will be thrown),
        // which can't be done if we do this in the super constructor,
        // since it has to be the first statement in this constructor,
        // which means this.msgRepo has to be initialised later.
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
}
