package controllers;

import gui.ChatFrame;
import gui.MessagePanel;
import models.message.Message;

import java.time.Instant;
import java.util.Date;
import java.util.List;


public abstract class AbstractChatController {

    private final static int NUMBER_OF_MESSAGES_TO_LOAD = 3;  // TODO 3 is easier to test/show, change to a higher number
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
    protected abstract boolean canUserDeleteThisMessage(Message msg);

    private MessagePanel makePanel(Message msg) {
        var msgPanel = new MessagePanel(msg);
        if (canUserDeleteThisMessage(msg)) {
            msgPanel.showDeleteButton();
            msgPanel.onClickDelete(() -> {
                if (! msgPanel.confirmDelete()) return;
                if (! removeMessage(msg)) {
                    msgPanel.warnCouldNotDelete();
                    return;
                }
                view.removeMessagePanel(msgPanel);
            });
        }
        return msgPanel;
    }

    private void loadNewMessages() {
        var newerMessages = getMessagesAfter(lastMessageQuery);
        lastMessageQuery = Date.from(Instant.now());
        if (newerMessages == null) {
            view.showErrorDialog("Could not load newer messages");
            return;
        }
        for (var msg : newerMessages)
            view.addMessagePanelBelow(makePanel(msg));
    }

    public void initialise() {
        // Why a separate initialise method instead of doing this in the constructor?
        // See GroupChatController.java
        var mostRecentMessages = getNewestMessages(NUMBER_OF_MESSAGES_TO_LOAD);

        lastMessageQuery  = Date.from(Instant.now());
        oldestMessageDate = Date.from(Instant.now());  // Default value for when there are no messages

        if (mostRecentMessages == null) {
            view.showErrorDialog("Could not load the most recent messages");
        } else {
            for (var msg : mostRecentMessages)
                view.addMessagePanelBelow(makePanel(msg));
            if (mostRecentMessages.size() > 0)
                oldestMessageDate = mostRecentMessages.get(0).sentAt();
        }

        view.onClickLoadOlder(() -> {
            var olderMessages = getMessagesBefore(oldestMessageDate, NUMBER_OF_MESSAGES_TO_LOAD);
            if (olderMessages == null) {
                view.showErrorDialog("Could not load older messages");
                return;
            }
            int n = olderMessages.size();
            // Push in reverse to show them in the right order
            for (int i = n-1; i >= 0; i--)
                view.addMessagePanelAbove(makePanel(olderMessages.get(i)));
            if (n > 0)
                oldestMessageDate = olderMessages.get(0).sentAt();
        });


        view.onClickLoadNewer(this::loadNewMessages);


        view.onClickSend(text -> {
            if (text.isEmpty()) {
                view.showErrorDialog("Can't send empty message");
                return;
            }
            if (! addMessage(text)) {
                view.showErrorDialog("Could not send the message");
                return;
            }
            loadNewMessages();
            view.clearMessageTextarea();
        });
    }

    public void onClose(Runnable action) {
        view.onClose(action);
    }

    public void display() {
        view.display();
    }


}
