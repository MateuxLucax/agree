package controllers;

import gui.FriendBar;
import gui.FriendListPanel;
import models.User;
import repositories.friendship.FriendshipRepository;

import javax.swing.*;

public class FriendListController
{
    private final FriendListPanel view;
    private final User user;
    private final FriendshipRepository friendRepo;

    public FriendListController(User user)
    {
        this.user       = user;
        this.view       = new FriendListPanel();
        this.friendRepo = new FriendshipRepository();

        loadFriendBars();

        view.onClickRefresh(() -> {
            view.clear();
            loadFriendBars();
            view.repaint();
            view.revalidate();
        });
    }

    public void loadFriendBars() {
        for (var friend : friendRepo.getFriends(user)) {
            var bar = new FriendBar(friend.getNickname());
            view.addFriendBar(bar);

            bar.onClickUnfriend(() -> {
                // TODO dialog: do you really want to unfriend <friendname>?
                if (friendRepo.removeFriend(user, friend)) {
                    view.removeFriendBar(bar);
                } else {
                    System.out.println("Couldn't remove friend (TODO dialog)"); // TODO
                }
            });

            bar.onClickChat(() -> {
                bar.chatButtonSetEnabled(false);
                var chatCon = new FriendChatController(user, friend);
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
