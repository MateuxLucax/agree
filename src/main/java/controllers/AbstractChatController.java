package controllers;

import gui.ChatFrame;
import gui.MessagePanel;
import models.message.Message;

import java.time.Instant;
import java.util.Date;
import java.util.List;


public abstract class AbstractChatController {

    private final static int NUMBER_OF_MESSAGES_TO_LOAD = 3; // TODO change to 50
    private final ChatFrame view;
    private Date oldestMessageDate;
    private Date lastMessageQuery;

    public AbstractChatController(String title) {
        view = new ChatFrame(title);
    }

    protected abstract List<Message> getNewestMessages(int numberOfMessages);
    protected abstract List<Message> getMessagesBefore(Date date, int numberOfMessages);
    protected abstract List<Message> getMessagesAfter(Date date);
    protected abstract boolean addMessage(String text);
    protected abstract boolean removeMessage(Message msg);
    protected abstract boolean isMessageDeleteable(Message msg);  // not sure if I like this

    private MessagePanel makePanel(Message msg) {
        var panel = new MessagePanel(msg);
        if (isMessageDeleteable(msg)) {
            panel.showDeleteButton();
            panel.onClickDelete(() -> {
                if (!removeMessage(msg)) {
                    // TODO dialog "couldn't delete message"
                    return;
                }
                view.removeMessage(panel);
            });
        }
        return panel;
    }

    private void loadNewMessages() {
        var newerMessages = getMessagesAfter(lastMessageQuery);
        if (newerMessages != null) {
            for (var msg : newerMessages)
                view.addMessageBelow(makePanel(msg));
            lastMessageQuery = Date.from(Instant.now());
        } // TODO else dialog "couldn't load newer messages"
    }

    public void initialise() {
        // Why a separate initialise method instead of doing this in the constructor?
        // See GroupChatController.java
        var mostRecentMessages = getNewestMessages(NUMBER_OF_MESSAGES_TO_LOAD);

        lastMessageQuery  = Date.from(Instant.now());
        oldestMessageDate = Date.from(Instant.now());  // Default value for when there are no messages

        if (mostRecentMessages != null) {
            for (var msg : mostRecentMessages)
                view.addMessageBelow(makePanel(msg));
            if (mostRecentMessages.size() > 0)
                oldestMessageDate = mostRecentMessages.get(0).sentAt();
        } // TODO else dialog "couldn't load the most recent messages"


        view.onClickLoadOlder(() -> {
            var olderMessages = getMessagesBefore(oldestMessageDate, NUMBER_OF_MESSAGES_TO_LOAD);
            if (olderMessages != null) {
                int n = olderMessages.size();
                // Push in reverse to show them in the right order
                for (int i = n-1; i >= 0; i--)
                    view.addMessageAbove(makePanel(olderMessages.get(i)));
                if (n > 0)
                    oldestMessageDate = olderMessages.get(0).sentAt();
            } // TODO else dialog "could not load older messages"
        });


        view.onClickLoadNewer(this::loadNewMessages);


        view.onClickSend(text -> {
            if (text.isEmpty()) {
                // TODO dialog "can't send empty message"
                return;
            }
            if (addMessage(text)) {
                loadNewMessages();
                view.clearMessageTextarea();
            } // TODO else dialog "couldn't send message"
        });
    }

    public void onClose(Runnable action) {
        view.onClose(action);
    }

    public void display() {
        view.display();
    }


}
