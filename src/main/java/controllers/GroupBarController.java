package controllers;

import gui.GroupBar;
import models.User;
import models.group.Group;

public class GroupBarController
{
    private User user;
    private Group group;
    private GroupBar view;
    private Runnable onDelete;

    public GroupBarController(User user, Group group)
    {
        this.user = user;
        this.group = group;
        this.view = new GroupBar(group.getName());

        view.onClickChat(() -> {
            new GroupMessagingController(user, group, view.getChatButton())
                    .display();
        });

        view.onClickMembers(() -> {
            new GroupMemberListController(user, group, view.getMembersButton())
                    .display();
        });

        view.onClickInvite(() -> {
            new GroupInviteFriendsController(user, group, view.getInviteButton())
                    .display();
        });

        if (group.isOwnedBy(user)) {
            view.showManageButton();
            view.onClickManage(() -> {
                var manageCon = new GroupManagementController(group, view.getManageButton());
                manageCon.onRename(newName -> {
                    rename(newName);
                    reload();
                });
                manageCon.onDelete(this.onDelete);
                manageCon.display();
            });
        }
    }

    public void onDelete(Runnable action)
    {
        this.onDelete = action;
    }

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
