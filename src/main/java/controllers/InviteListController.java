package controllers;

import gui.InviteBar;
import gui.InviteListFrame;
import models.User;
import repositories.friendship.FriendshipRepository;
import repositories.group.GroupRepository;
import repositories.invite.InviteRepository;

public class InviteListController
{
    private final InviteListFrame view;

    public InviteListController(User user)
    {
        view = new InviteListFrame();

        var inviteRepo  = new InviteRepository();
        var groupRepo   = new GroupRepository();
        var friendRepo  = new FriendshipRepository();

        for (var inv : inviteRepo.getInvites(user)) {
            var bar = new InviteBar(inv);
            view.addInviteBar(bar);

            if (inv.to(user)) {
                bar.showAcceptAndDeclineButtons();
                bar.onClickAccept(() -> {

                    // TODO

                });

                bar.onClickDecline(() -> {

                    // TODO

                });
            }
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
