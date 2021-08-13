package controllers.group;

import gui.ChatFrame;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.message.MessageRepository;

import java.time.Instant;
import java.util.Date;

public class GroupChatController
{
    private final static int NUMBER_OF_MESSAGES_TO_LOAD = 3;  // TODO change to 50

    private final Group group;
    private final ChatFrame view;
    private final MessageRepository msgRepo;

    private Date oldestMessageDate;
    private Date lastMessageQuery;

    public GroupChatController(User user, Group group)
    {
        this.group   = group;
        this.view    = new ChatFrame(group.getName() + " (group): chat");
        this.msgRepo = new MessageRepository();


        var newestMessages = msgRepo.getNewestGroupMessages(group, NUMBER_OF_MESSAGES_TO_LOAD);
        lastMessageQuery  = Date.from(Instant.now());
        oldestMessageDate = Date.from(Instant.now());  // Default value for when there are no messages
        if (newestMessages != null) {
            if (newestMessages.size() > 0) {
                view.addMessagesBelow(newestMessages);
                oldestMessageDate = newestMessages.get(0).sentAt();
            }
        } // TODO else dialog "couldn't load the most recent messages"

        view.onLoadOlder(() -> {
            var olderMessages = msgRepo.getGroupMessagesBefore(group, oldestMessageDate, NUMBER_OF_MESSAGES_TO_LOAD);
            if (olderMessages != null) {
                if (olderMessages.size() > 0) {
                    view.addMessagesAbove(olderMessages);
                    oldestMessageDate = olderMessages.get(0).sentAt();
                }
             } // TODO else dialog "could not load older messages"
        });

        view.onLoadNewer(this::loadNewMessages);

        view.onSendMessage(text -> {
            if (text.isEmpty()) {
                // TODO dialog "can't send empty message"
                return;
            }
            var msg = new Message(user, text, new Date());
            if (msgRepo.addGroupMessage(group, msg)) {
                loadNewMessages();  // Other messages might've been sent in the meantime
                view.clearMessageTextarea();
            } // TODO else dialog "couldn't send message"
        });
    }

    public void loadNewMessages() {
        var newerMessages = msgRepo.getGroupMessagesAfter(group, lastMessageQuery);
        if (newerMessages != null) {
            view.addMessagesBelow(newerMessages);
            lastMessageQuery = Date.from(Instant.now());
        } // TODO else dialog "couldn't load newer messages"
    }

    public void onClose(Runnable action) {
        view.onClose(action);
    }

    public void display() {
        view.display();
    }
}
