package controllers;

import gui.InviteBar;
import gui.InviteListFrame;
import models.User;
import repositories.invite.IInviteRepository;
import repositories.invite.InviteRepository;

public class InviteListController
{
    private final InviteListFrame view;
    private final IInviteRepository inviteRepo;

    public InviteListController(User user)
    {
        view = new InviteListFrame();
        inviteRepo  = new InviteRepository();

        for (var inv : inviteRepo.getInvites(user)) {
            var bar = new InviteBar(inv);
            view.addInviteBar(bar);

            if (inv.to(user)) {
                Runnable onClickAccept = () -> {
                    if (! bar.confirmAccept(inv)) return;
                    if (! inviteRepo.acceptInvite(inv)) {
                        bar.warnCouldNotAcceptInvite();
                        return;
                    }
                    view.removeInviteBar(bar);
                };
                Runnable onClickDecline = () -> {
                    if (! bar.confirmDecline(inv)) return;
                    if (! inviteRepo.removeInvite(inv)) {
                        bar.warnCouldNotDeclineInvite();
                        return;
                    }
                    view.removeInviteBar(bar);
                };
                bar.addAcceptAndDeclineButtons(onClickAccept, onClickDecline);
            }
            else {  // inv.from(user)
                bar.addCancelButton(() -> {
                    if (! bar.confirmCancel(inv)) return;
                    if (! inviteRepo.removeInvite(inv)) {
                        bar.warnCouldNotCancelInvite();
                        return;
                    }
                    view.removeInviteBar(bar);
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
