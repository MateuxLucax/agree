package controllers;

import gui.InviteBar;
import gui.InviteListFrame;
import models.User;
import models.invite.FriendInvite;
import models.invite.GroupInvite;
import repositories.friendship.FriendshipRepository;
import repositories.friendship.IFriendshipRepository;
import repositories.group.GroupRepository;
import repositories.group.IGroupRepository;
import repositories.invite.IInviteRepository;
import repositories.invite.InviteRepository;

public class InviteListController
{
    private final InviteListFrame view;
    private final IInviteRepository     inviteRepo;
    private final IGroupRepository      groupRepo;
    private final IFriendshipRepository friendRepo;

    public InviteListController(User user)
    {
        view = new InviteListFrame();

        inviteRepo  = new InviteRepository();
        groupRepo   = new GroupRepository();
        friendRepo  = new FriendshipRepository();

        for (var inv : inviteRepo.getInvites(user)) {
            var bar = new InviteBar(inv);
            view.addInviteBar(bar);

            if (!inv.to(user))
                continue;

            bar.showAcceptAndDeclineButtons();
            // TODO for each, add a dialog saying invite accepted or something nicer
            if (inv instanceof GroupInvite) {
                bar.onClickAccept(() -> {
                    if (inviteRepo.acceptGroupInviteAndAddMember((GroupInvite) inv)) {
                        view.removeInviteBar(bar);
                    }
                });
            } else {
                bar.onClickAccept(() -> handleFriendInviteAccepted((FriendInvite) inv, bar));
            }

            bar.onClickDecline(() -> {
                if (inviteRepo.declineInvite(inv)) {
                    view.removeInviteBar(bar);
                }
            });
        }
    }

    public void handleFriendInviteAccepted(FriendInvite inv, InviteBar bar)
    {
        // Add as friends
        if (!friendRepo.addFriend(inv.from(), inv.to())) {
            // TODO dialog could not accept
            return;
        }
        // Remove invite from database
        if (!inviteRepo.declineInvite(inv)) {
            // TODO dialog could not accept
            // Since adding the friend was successful but
            // not the whole operation, remove the added friend.
            friendRepo.removeFriend(inv.from(), inv.to());
            // TODO What if removeFriend fails?
            return;
        }
        // At this point, we know the database was updated successfully
        view.removeInviteBar(bar);
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
