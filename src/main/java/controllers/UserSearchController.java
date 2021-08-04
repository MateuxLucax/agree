package controllers;

import gui.UserBar;
import gui.UserSearchFrame;
import models.User;
import models.invite.FriendshipInvite;
import models.invite.InviteState;
import repositories.friendship.FriendshipRepository;
import repositories.invite.InviteRepositoryTest;
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
        var inviteRepo = new InviteRepositoryTest();

        var friends = friendRepo.getFriends(userInSession);
        var pendingFriendInvs = inviteRepo.getFriendInvites(userInSession, InviteState.PENDING);

        view.onSearch(text -> {
            view.clearResults();  // Otherwise results from previous searches will remain
            List<User> searchResults = userRepo.searchUsers(text);
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
                        if (inviteRepo.addInvite(inv)) {
                            pendingFriendInvs.add(inv);
                            bar.replaceWithInviteSentButton();
                        }
                        // TODO else dialog "couldn't send invite" (also, some other places need dialogs like this too)
                    });
                }
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
