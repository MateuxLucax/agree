package controllers.group;

import gui.group.GroupBar;
import models.User;
import models.group.Group;
import repositories.group.GroupRepository;
import repositories.group.IGroupRepository;

public class GroupBarController
{
    private final GroupBar view;

    private final User  user;
    private final Group group;
    private final IGroupRepository groupRepo;

    private Runnable afterDelete;
    private Runnable afterQuit;

    public GroupBarController(User user, Group group)
    {
        this.view  = new GroupBar(group.getName());
        this.user  = user;
        this.group = group;
        this.groupRepo = new GroupRepository();

        view.onClickChat(() -> {
            var con = new GroupChatController(user, group);
            view.getChatButton().setEnabled(false);
            con.onClose(() -> view.getChatButton().setEnabled(true));
            con.display();
        });

        view.onClickMembers(() -> {
            var con = new GroupMemberListController(user, group);
            con.afterChangeOwner(() -> {
                view.replaceManageWithQuitButton();
                view.onClickQuit(this::quit);
            });
            view.getMembersButton().setEnabled(false);
            con.onClose(() -> view.getMembersButton().setEnabled(true));
            con.display();
        });

        view.onClickInvite(() -> {
            var con = new GroupInviteFriendsController(user, group);
            view.getInviteButton().setEnabled(false);
            con.onClose(() -> view.getInviteButton().setEnabled(true));
            con.display();
        });

        if (group.ownedBy(user)) {
            view.showManageButton();
            view.onClickManage(() -> {
                var con = new GroupManagementController(group);
                con.afterDelete(this.afterDelete);
                con.afterRename(view::rename);
                view.getManageButton().setEnabled(false);
                con.onClose(() -> view.getManageButton().setEnabled(true));
                con.display();
            });
        }
        else {
            view.showQuitButton();
            view.onClickQuit(this::quit);
        }
    }

    public void quit() {
        if (! groupRepo.removeMember(group, user)) {
            // TODO dialog "couldn't remove member"
            return;
        }
        if (afterQuit != null) afterQuit.run();
    }

    public void afterDelete(Runnable action) {
        this.afterDelete = action;
    }

    public void afterQuit(Runnable action) {
        this.afterQuit = action;
    }

    public GroupBar getBar() {
        return view;
    }
}
