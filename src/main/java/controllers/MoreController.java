package controllers;

import gui.MorePanel;
import models.User;

public class MoreController
{
    private final MorePanel view;

    public MoreController(User user)
    {
        view = new MorePanel();

        view.onClickSearchButton(() -> {
            view.searchButtonSetEnabled(false);
            var searchCon = new UserSearchController(user);
            searchCon.onClose(() -> view.searchButtonSetEnabled(true));
            searchCon.display();
        });

        view.onClickInvitesButton(() -> {
            view.invitesButtonSetEnabled(false);
            var invitesCon = new InviteListController(user);
            invitesCon.onClose(() -> view.invitesButtonSetEnabled(true));
            invitesCon.display();
        });

        view.onClickUsgButton(() -> {
            view.usgButtonSetEnabled(false);
            var usgCon = new UsersInSameGroupsController(user);
            usgCon.onClose(() -> view.usgButtonSetEnabled(true));
            usgCon.display();
        });
    }

    public MorePanel getPanel()
    {
        return view;
    }
}
