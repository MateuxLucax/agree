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
                if (! bar.confirmUnfriend()) return;
                if (! friendRepo.removeFriend(user, friend)) {
                    bar.warnCouldNotUnfriend();
                    return;
                }
                view.removeFriendBar(bar);
            });

            bar.onClickChat(() -> {
                var chatCon = new FriendChatController(user, friend);
                bar.chatButtonSetEnabled(false);
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
