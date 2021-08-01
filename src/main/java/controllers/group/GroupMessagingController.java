package controllers.group;

import gui.MessagingFrame;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageInFileRepository;

import javax.swing.*;
import java.util.Date;

public class GroupMessagingController {

    private final MessagingFrame view;
    private final IMessageRepository msgRepo;

    public GroupMessagingController(User user, Group group)
    {
        this.view = new MessagingFrame();
        this.msgRepo = new MessageInFileRepository();

        view.loadMessages(group.getMessages());

        view.onLoadOlder(() -> {
            msgRepo.getMessagesBefore(group, group.oldestMessageDate());
            view.loadMessages(group.getMessages());
        });

        view.onLoadNewer(() -> {
            msgRepo.getMessagesAfter(group, group.newestMessageDate());
            view.loadMessages(group.getMessages());
        });

        view.onSendMessage(text -> {
            var msg = new Message(user, text, new Date());
            boolean ok = msgRepo.addMessage(group, msg);
            if (ok) {
                group.loadMessageBelow(msg);
                view.loadMessages(group.getMessages());
            }
            return ok;
        });
    }

    public void onClose(Runnable action)
    {
        view.onClose(action);
    }

    public void display()
    {
        view.display();
    }
}
