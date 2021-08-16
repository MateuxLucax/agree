package controllers;

import gui.UserBar;
import models.User;
import models.invite.FriendInvite;
import repositories.invite.IInviteRepository;

import java.util.List;

public class UserBarController {

    public static void setupUserBar(
            UserBar bar,
            User userInBar,
            List<User> friends,
            List<FriendInvite> pendingFriendInvites,
            IInviteRepository inviteRepo,
            User userInSession)
    {
        if (userInBar.equals(userInSession)) {
            return;
        }

        if (friends.contains(userInBar)) {
            bar.showAlreadyFriends();
        } else if (pendingFriendInvites.stream().anyMatch(i -> i.from(userInBar) || i.to(userInBar))) {
            bar.showInviteSent();
        } else {
            bar.addAskToBeFriendsButton(() -> {
                var inv = new FriendInvite(userInSession, userInBar);
                if (! inviteRepo.addInvite(inv)) {
                    bar.warnCouldNotSendInvite();
                    return;
                }
                pendingFriendInvites.add(inv); // TODO Is this step necessary?
                bar.updateInviteSituation();
            });
        }
    }
}
