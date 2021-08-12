package controllers.group;

import gui.ChatFrame;
import models.User;
import models.group.Group;
import models.message.Message;
import repositories.message.MessageRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class GroupChatController
{
    private final static int NUMBER_OF_MESSAGES_TO_LOAD = 3;  // TODO change to 50

    private final Group group;
    private final ChatFrame view;
    private final MessageRepository msgRepo;

    private Date oldestMessageDate;
    private Date lastMessageQuery;

    // TODO!! deal with the edge case of the group that was just created and has no messages yet
    //   specially in regards to the "load older messages" button,
    //   treat the cases of: when it has no messages
    //                  and: after the users adds one or more messages

    public GroupChatController(User user, Group group)
    {
        this.group   = group;
        this.view    = new ChatFrame(group.getName() + " (group): chat");
        this.msgRepo = new MessageRepository();

        var newestMessages = msgRepo.getNewestGroupMessages(group, NUMBER_OF_MESSAGES_TO_LOAD);
        lastMessageQuery = Date.from(Instant.now());
        // Don't && the following two conditions together -- null means the database failed to load them,
        // in which case we show a dialog. size == 0 means the database loaded them as usual, but that
        // there just weren't any messages -- no dialog needed, no error happened.
        if (newestMessages != null) {
            if (newestMessages.size() > 0) {
                view.addMessagesBelow(newestMessages);
                oldestMessageDate = newestMessages.get(0).sentAt();
            }
        } // TODO else dialog "couldn't load the most recent messages"

        view.onLoadOlder(() -> {
            var olderMessages = msgRepo.getGroupMessagesBefore(group, oldestMessageDate, NUMBER_OF_MESSAGES_TO_LOAD);
            // Don't && the following two conditions together; explanation above.
            if (olderMessages != null) {
                if (olderMessages.size() > 0) {
                    view.addMessagesAbove(olderMessages);
                    oldestMessageDate = olderMessages.get(0).sentAt();
                }
             } // TODO else dialog "could not load older messages"
        });

        view.onLoadNewer(this::loadNewMessages);

        view.onSendMessage(text -> {
            if (text.isEmpty())
                return; // TODO dialog "can't send empty message"
            var msg = new Message(user, text, new Date());
            if (msgRepo.addGroupMessage(group, msg)) {
                // Other messages might've been sent by other users in the meantime,
                // so we can't only load this new message
                loadNewMessages();
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
