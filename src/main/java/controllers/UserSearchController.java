package controllers;

import gui.UserBar;
import gui.UserSearchFrame;
import models.User;
import repositories.friendship.FriendshipRepository;
import repositories.invite.InviteRepository;
import repositories.user.UserRepository;

import java.util.List;

public class UserSearchController
{
    private final UserSearchFrame view;

    public UserSearchController(User userInSession)
    {
        this.view = new UserSearchFrame();

        var userRepo   = new UserRepository();
        var friendRepo = new FriendshipRepository();
        var inviteRepo = new InviteRepository();

        var friends = friendRepo.getFriends(userInSession);
        var friendInvs = inviteRepo.getFriendInvites(userInSession);

        view.onSearch(text -> {
            view.clearResults();  // Otherwise results from previous searches will remain
            List<User> searchResults = userRepo.searchUsers(text);
            for (var res : searchResults) {
                var bar = new UserBar(res.getNickname());
                view.addUserBar(bar);

                UserBarController.setupUserBar(
                        bar, res, friends,
                        friendInvs, inviteRepo, userInSession
                );
            }
            view.pack();
        });
    }

    public void onClose(Runnable action)
    {
        this.view.onClose(action);
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }
}
