package controllers;

import gui.MessagingFrame;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageInFileRepository;

import javax.swing.*;
import java.util.Date;

public class GroupMessagingController {

    private MessagingFrame view;
    private IMessageRepository msgRepo;
    private User user;
    private Group group;

    public GroupMessagingController(User user, Group group, JButton btnThatOpenedTheFrame)
    {
        view = new MessagingFrame(btnThatOpenedTheFrame);
        this.user = user;
        this.group = group;
        msgRepo = new MessageInFileRepository();

        view.loadMessages(group.getMessages());
        view.onLoadOlder(this::loadOlder);
        view.onLoadNewer(this::loadNewer);
        view.onSendMessage(this::sendMessage);
    }

    public void loadOlder()
    {
        msgRepo.getMessagesBefore(group, group.oldestMessageDate());
        view.loadMessages(group.getMessages());
    }

    public void loadNewer()
    {
        msgRepo.getMessagesAfter(group, group.newestMessageDate());
        view.loadMessages(group.getMessages());
    }

    public boolean sendMessage(String text)
    {
        var msg = new Message(user, text, new Date());
        boolean ok = msgRepo.addMessage(group, msg);
        if (ok) {
            group.loadMessageBelow(msg);
            view.loadMessages(group.getMessages());
        }
        return ok;
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }
}
