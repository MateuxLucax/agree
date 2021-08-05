package gui;

import models.User;

public class FriendMessagingController
{
    private final MessagingFrame view;

    public FriendMessagingController(User user, User friend)
    {
        view = new MessagingFrame(friend.getNickname() + " (friend): chat");

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
