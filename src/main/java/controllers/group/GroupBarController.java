package controllers.group;

import gui.group.GroupBar;
import models.User;
import models.group.Group;

public class GroupBarController
{
    private final GroupBar view;
    private Runnable onDelete;

    public GroupBarController(User user, Group group)
    {
        this.view = new GroupBar(group.getName());

        view.onClickChat(() -> {
            view.getChatButton().setEnabled(false);
            var con = new GroupMessagingController(user, group);
            con.onClose(() -> view.getChatButton().setEnabled(true));
            con.display();
        });

        view.onClickMembers(() -> {
            view.getMembersButton().setEnabled(false);
            var con = new GroupMemberListController(user, group);
            con.onClose(() -> view.getMembersButton().setEnabled(true));
            con.display();
        });

        view.onClickInvite(() -> {
            view.getInviteButton().setEnabled(false);
            var con = new GroupInviteFriendsController(user, group);
            con.onClose(() -> view.getInviteButton().setEnabled(true));
            con.display();
        });

        if (group.isOwnedBy(user)) {
            view.showManageButton();
            view.onClickManage(() -> {
                view.getManageButton().setEnabled(false);
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
