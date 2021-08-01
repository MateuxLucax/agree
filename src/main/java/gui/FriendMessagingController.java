package gui;

import repositories.message.IMessageRepository;

public class FriendMessagingController
{
    private final MessagingFrame view;

    public FriendMessagingController()
    {
        view = new MessagingFrame();

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
