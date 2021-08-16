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
            con.afterChangeOwner(() -> view.replaceManageWithQuitButton(this::quit));
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
            view.addManageButton(() -> {
                var con = new GroupManagementController(group);
                con.afterDelete(this.afterDelete);
                con.afterRename(view::rename);
                view.getManageButton().setEnabled(false);
                con.onClose(() -> view.getManageButton().setEnabled(true));
                con.display();
            });
        }
        else {
            view.addQuitButton(this::quit);
        }
    }

    public void quit() {
        if (! view.confirmQuit()) return;
        if (! groupRepo.removeMember(group, user)) {
            view.warnCouldNotQuit();
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
