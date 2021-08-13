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

    private Runnable onDelete;
    private Runnable onQuit;

    public GroupBarController(User user, Group group)
    {
        this.view  = new GroupBar(group.getName());
        this.user  = user;
        this.group = group;
        this.groupRepo = new GroupRepository();

        view.onClickChat(() -> {
            view.getChatButton().setEnabled(false);
            var con = new GroupChatController(user, group);
            con.onClose(() -> view.getChatButton().setEnabled(true));
            con.display();
        });

        view.onClickMembers(() -> {
            view.getMembersButton().setEnabled(false);
            var con = new GroupMemberListController(user, group);
            con.onChangeOwner(() -> {
                view.replaceManageWithQuitButton();
                view.onClickQuit(this::quit);
            });
            con.onClose(() -> view.getMembersButton().setEnabled(true));
            con.display();
        });

        view.onClickInvite(() -> {
            view.getInviteButton().setEnabled(false);
            var con = new GroupInviteFriendsController(user, group);
            con.onClose(() -> view.getInviteButton().setEnabled(true));
            con.display();
        });

        if (group.ownedBy(user)) {
            view.showManageButton();
            view.onClickManage(() -> {
                view.getManageButton().setEnabled(false);
                var con = new GroupManagementController(group);
                con.onClose(() -> view.getManageButton().setEnabled(true));
                // GroupManagementButton does the actual renaming
                con.onRename(newName -> {
                    view.rename(newName);
                    view.repaint();
                    view.revalidate();
                });
                con.onDelete(this.onDelete);
                con.display();
            });
        }
        else {
            view.showQuitButton();
            view.onClickQuit(this::quit);
        }

    }

    public void quit() {
        if (groupRepo.removeMember(group, user) && onQuit != null) {
            onQuit.run();
        }
    }

    public void onDelete(Runnable action) {
        this.onDelete = action;
    }

    public void onQuit(Runnable action) {
        this.onQuit = action;
    }

    public GroupBar getBar() {
        return view;
    }
}
