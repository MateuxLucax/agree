package gui;

import controllers.GroupInviteController;
import controllers.GroupManagementController;
import controllers.GroupMessagingController;
import models.User;
import models.group.Group;

import javax.swing.*;

public class GroupBarController
{
    private User user;
    private Group group;
    private GroupBar view;

    public GroupBarController(User user, Group group)
    {
        this.user = user;
        this.group = group;
        this.view = new GroupBar(user, group);
        view.onClickChat(this::chat);
        view.onClickInvite(this::inviteFriends);
        if (group.isOwnedBy(user)) {
            view.showManageButton();
            view.onClickManage(this::manage);
        }
    }

    public GroupBar getBar()
    {
        return view;
    }

    public void chat()
    {
        var chatCon = new GroupMessagingController(user, group, view.getChatButton());
        chatCon.display();
    }

    public void inviteFriends()
    {
        var groupInviteCon = new GroupInviteController(user, group, view.getInviteButton());
        groupInviteCon.display();
    }

    public void manage()
    {
        var manageCon = new GroupManagementController(group, view.getManageButton());
        manageCon.display();
    }

    public void rename(String newName)
    {
        view.rename(newName);
    }
}
