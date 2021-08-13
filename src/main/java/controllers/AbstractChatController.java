package controllers;

import gui.ChatFrame;
import models.message.Message;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public abstract class AbstractChatController {

    private final static int NUMBER_OF_MESSAGES_TO_LOAD = 3; // TODO change to 50
    private final ChatFrame view;
    private Date oldestMessageDate;
    private Date lastMessageQuery;

    protected abstract List<Message> getNewestMessages(int numberOfMessages);
    protected abstract List<Message> getMessagesBefore(Date date, int numberOfMessages);
    protected abstract List<Message> getMessagesAfter(Date date);
    protected abstract boolean addMessage(String text);

    private void loadNewMessages() {
        var newerMessages = getMessagesAfter(lastMessageQuery);
        if (newerMessages != null) {
            view.addMessagesBelow(newerMessages);
            lastMessageQuery = Date.from(Instant.now());
        } // TODO else dialog "couldn't load newer messages"
    }

    public AbstractChatController(String title) {
        view = new ChatFrame(title);
    }

    public void initialise() {
        // Why a separate initialise method instead of doing this in the constructor?
        // See GroupChatController.java
        var mostRecentMessages = getNewestMessages(NUMBER_OF_MESSAGES_TO_LOAD);

        lastMessageQuery  = Date.from(Instant.now());
        oldestMessageDate = Date.from(Instant.now());  // Default value for when there are no messages

        if (mostRecentMessages != null) {
            if (mostRecentMessages.size() > 0) {
                view.addMessagesBelow(mostRecentMessages);
                oldestMessageDate = mostRecentMessages.get(0).sentAt();
            }
        } // TODO else dialog "couldn't load the most recent messages"


        view.onLoadOlder(() -> {
            var olderMessages = getMessagesBefore(oldestMessageDate, NUMBER_OF_MESSAGES_TO_LOAD);
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
