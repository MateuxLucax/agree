package controllers.group;

import gui.group.GroupBar;
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
            var con = new GroupMessagingController(user, group);
            con.onClose(() -> view.getChatButton().setEnabled(true));
            con.display();
        });

        view.onClickMembers(() -> {
            var con = new GroupMemberListController(user, group);
            con.onClose(() -> view.getMembersButton().setEnabled(true));
            con.display();
        });

        view.onClickInvite(() -> {
            var con = new GroupInviteFriendsController(user, group);
            con.onClose(() -> view.getInviteButton().setEnabled(true));
            con.display();
        });

        if (group.isOwnedBy(user)) {
            view.showManageButton();
            view.onClickManage(() -> {
                var con = new GroupManagementController(group);
                con.onClose(() -> view.getManageButton().setEnabled(true));
                con.onRename(newName -> {
                    view.rename(newName);
                    view.repaint();
                    view.revalidate();
                });
                con.onDelete(this.onDelete);
                con.display();
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
}
