package controllers;

import gui.GroupBar;
import models.User;
import models.group.Group;

public class GroupBarController
{
    private User user;
    private Group group;
    private GroupBar view;

    public GroupBarController(User user, Group group)
    {
        this.user = user;
        this.group = group;
        this.view = new GroupBar(group.getName());

        view.onClickChat(() -> {
            new GroupMessagingController(user, group, view.getChatButton()).display();
        });

        view.onClickInvite(() -> {
            new GroupInviteFriendsController(this.user, this.group, view.getInviteButton()).display();
        });

        if (group.isOwnedBy(user)) {
            view.showManageButton();
            view.onClickManage(() -> {
                new GroupManagementController(this.group, this, view.getManageButton()).display();
            });
        }
    }

    // TODO this method only exists because we need to add the bar
    //   to the panel listing the group bars, so consider removing
    //   it when we make a controller for that panel
    public GroupBar getBar()
    {
        return view;
    }

    public void reload()
    {
        view.repaint();
        view.revalidate();
    }

    public void rename(String newName)
    {
        view.rename(newName);
    }
}
