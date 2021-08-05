package controllers;

import gui.UserBar;
import models.User;
import models.invite.FriendInvite;
import repositories.invite.IInviteRepository;

import java.util.List;

public class UserBarController {

    public static void setupUserBar(
            User userInSession,
            User userInBar,
            List<User> friends,
            List<FriendInvite> pendingFriendInvites,
            IInviteRepository inviteRepo,
            UserBar bar)
    {
        if (userInBar.equals(userInSession)) {
            return;
        }

        if (friends.contains(userInBar)) {
            bar.showAlreadyFriendsButton();
        } else if (pendingFriendInvites.stream().anyMatch(i -> i.involves(userInBar))) {
            bar.showInviteSentButton();
        } else {
            bar.showAskToBeFriendsButton();
            bar.onClickAskToBeFriends(() -> {
                var inv = new FriendInvite(userInSession, userInBar);
                if (inviteRepo.addInvite(inv)) {
                    pendingFriendInvites.add(inv);
                    bar.replaceWithInviteSentButton();
                }
                // TODO else dialog "couldn't send invite" (also, some other places need dialogs like this too)
            });
        }
    }
}
