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

    public InviteListController(User user)
    {
        view = new InviteListFrame();
        inviteRepo  = new InviteRepository();

        for (var inv : inviteRepo.getInvites(user)) {
            var bar = new InviteBar(inv);
            view.addInviteBar(bar);

            if (!inv.to(user))
                continue;

            bar.showAcceptAndDeclineButtons();
            bar.onClickAccept(() -> {
                if (inviteRepo.acceptInvite(inv))
                    view.removeInviteBar(bar);
            });
            bar.onClickDecline(() -> {
                if (inviteRepo.declineInvite(inv))
                    view.removeInviteBar(bar);
            });
        }
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
