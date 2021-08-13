package controllers;

import models.User;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageRepository;

import java.util.Date;
import java.util.List;

public class FriendChatController extends AbstractChatController
{
    private final User user;
    private final User friend;
    private final IMessageRepository msgRepo;

    public FriendChatController(User user, User friend) {
        super(friend.getNickname()+" (friend): chat");
        this.user    = user;
        this.friend  = friend;
        this.msgRepo = new MessageRepository();
        initialise();
    }

    @Override
    protected List<Message> getNewestMessages(int numberOfMessages) {
        return msgRepo.getNewestMessages(user, friend, numberOfMessages);
    }

    @Override
    protected List<Message> getMessagesBefore(Date date, int numberOfMessages) {
        return msgRepo.getMessagesBefore(user, friend, date, numberOfMessages);
    }

    @Override
    protected List<Message> getMessagesAfter(Date date) {
        return msgRepo.getMessagesAfter(user, friend, date);
    }

    @Override
    protected boolean addMessage(String text) {
        var msg = new Message(user, text, new Date());
        return msgRepo.addMessages(user, friend, msg);
    }
}
