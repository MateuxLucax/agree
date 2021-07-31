package controllers;

import gui.InviteBar;
import gui.InviteListFrame;
import models.User;
import models.invite.InviteState;
import repositories.invite.InviteRepositoryTest;

public class InviteListController
{
    private final InviteListFrame view;

    public InviteListController(User user)
    {
        view = new InviteListFrame();

        var invRepo = new InviteRepositoryTest();
        for (var inv : invRepo.getInvites(user)) {
            var bar = new InviteBar(inv);
            view.addInviteBar(bar);

            if (inv.to().equals(user) && inv.getState().equals(InviteState.PENDING)) {
                bar.showAcceptAndDeclineButtons();

                bar.onClickAccept(() -> {
                    inv.setState(InviteState.ACCEPTED);
                    if (invRepo.updateInvite(inv)) {
                        bar.updateStateButton();
                        bar.removeAcceptAndDeclineButtons();
                    } else {
                        // TODO show a dialog telling that we couldn't accept the invite
                        inv.setState(InviteState.PENDING);  // Back to pending
                    }
                });

                bar.onClickDecline(() -> {
                    inv.setState(InviteState.DECLINED);
                    if (invRepo.updateInvite(inv)) {
                        bar.updateStateButton();
                        bar.removeAcceptAndDeclineButtons();
                    } else {
                        // TODO show a dialog telling that we couldn't accept the invite
                        inv.setState(InviteState.PENDING);  // Back to pending
                    }
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
