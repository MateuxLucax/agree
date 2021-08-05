package controllers;

import gui.FriendBar;
import gui.FriendListPanel;
import gui.FriendMessagingController;
import models.User;
import repositories.friendship.FriendshipRepository;

import javax.swing.*;

public class FriendListController
{
    private final FriendListPanel view;

    public FriendListController(User user)
    {
        view = new FriendListPanel();

        var friendRepo = new FriendshipRepository();
        var friends = friendRepo.getFriends(user);

        for (var friend : friends) {
            var bar = new FriendBar(friend.getNickname());
            view.addFriendBar(bar);

            bar.onClickUnfriend(() -> {
                // TODO dialog: do you really want to unfriend <friendname>?
                if (friendRepo.removeFriend(user, friend))
                    view.removeFriendBar(bar);
                else
                    System.out.println("Couldn't remove friend (TODO dialog)"); // TODO
            });

            bar.onClickChat(() -> {
                bar.chatButtonSetEnabled(false);
                var chatCon = new FriendMessagingController(user, friend);
                chatCon.onClose(() -> bar.chatButtonSetEnabled(true));
                chatCon.display();
            });

        }
    }

    public JPanel getPanel()
    {
        return view;
    }

}
