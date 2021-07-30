package controllers;

import gui.UserBar;
import gui.UserSearchFrame;
import models.User;
import models.invite.FriendshipInvite;
import models.invite.InviteState;
import repositories.friendship.FriendshipInFileRepository;
import repositories.invite.InviteRepositoryInFile;
import repositories.user.UserRepositoryInFile;

import javax.swing.*;
import java.util.List;

public class UserSearchController
{
    private final UserSearchFrame view;

    public UserSearchController(User userInSession, JButton btnThatOpenedTheFrame)
    {
        this.view = new UserSearchFrame(btnThatOpenedTheFrame);

        var userRepo   = new UserRepositoryInFile();
        var friendRepo = new FriendshipInFileRepository();
        var inviteRepo = new InviteRepositoryInFile();

        var friends = friendRepo.getFriends(userInSession);
        var pendingFriendInvs = inviteRepo.getFriendInvites(userInSession, InviteState.PENDING);

        view.onSearch(text -> {
            view.clearResults();  // Otherwise results from previous searches will remain
            List<User> searchResults = userRepo.searchUser(text);
            for (var res : searchResults) {
                var bar = new UserBar(res.getNickname());
                view.addUserBar(bar);
                if (res.equals(userInSession)) {
                    continue;
                } else if (friends.contains(res)) {
                    bar.showAlreadyFriendsButton();
                } else if (pendingFriendInvs.stream().anyMatch(i -> i.involves(res))) {
                    bar.showInviteSentButton();
                } else {
                    bar.showAskToBeFriendsButton();
                    bar.onClickAskToBeFriends(() -> {
                        var inv = new FriendshipInvite(userInSession, res, InviteState.PENDING);
                        if (inviteRepo.addInvite(inv))
                            bar.replaceWithInviteSentButton();
                        // TODO else dialog "couldn't send invite" (also, some other places need dialogs like this too)
                    });
                }
            }
            view.pack();
        });
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }
}
