package controllers.group;

import gui.ChatFrame;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.message.IMessageRepository;
import repositories.message.MessageInFileRepository;

import java.util.Date;

public class GroupChatController
{

    private final ChatFrame view;
    private final IMessageRepository msgRepo;

    public GroupChatController(User user, Group group)
    {
        this.view = new ChatFrame(group.getName() + " (group): chat");
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
