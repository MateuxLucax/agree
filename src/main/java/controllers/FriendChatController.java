package controllers;

import gui.ChatFrame;
import models.User;

public class FriendChatController
{
    private final ChatFrame view;

    public FriendChatController(User user, User friend)
    {
        view = new ChatFrame(friend.getNickname() + " (friend): chat");

        // TODO (requires IMessageRepository supporting
        //   messagens between friends)
        /*
        view.loadMessages();

        view.onLoadOlder(() -> {
        });

        view.onLoadNewer(() -> {
        });

        view.onSendMessage(text -> {
        });
         */
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
