package controllers.group;

import controllers.UserBarController;
import gui.UserListFrame;
import gui.group.MemberBar;
import models.User;
import models.group.Group;
import repositories.friendship.FriendshipRepository;
import repositories.group.GroupRepository;
import repositories.group.IGroupRepository;
import repositories.invite.InviteRepository;

public class GroupMemberListController {

    private final UserListFrame view;
    private final IGroupRepository groupRepo;
    private Runnable afterChangeOwner;

    public GroupMemberListController(User userInSession, Group group)
    {
        view = new UserListFrame(group.getName() + ": members");
        groupRepo = new GroupRepository();

        var inviteRepo = new InviteRepository();

        var friends = (new FriendshipRepository()).getFriends(userInSession);
        var pendingFriendInvites = inviteRepo.getFriendInvites(userInSession);

        var owner = group.getOwner();
        var ownerBar = new MemberBar(owner.getNickname());
        view.addUserBar(ownerBar);
        ownerBar.showThatIsOwner();
        UserBarController.setupUserBar(
                ownerBar, owner, friends,
                pendingFriendInvites, inviteRepo, userInSession
        );

        for (var member : groupRepo.getMembers(group)) {
            var bar = new MemberBar(member.getNickname());
            view.addUserBar(bar);

            if (group.ownedBy(userInSession)) {
                bar.addRemoveButton(() -> {
                    String memberNickname = member.getNickname();
                    if (! bar.confirmRemove(memberNickname)) return;
                    if (! groupRepo.removeMember(group, member)) {
                        bar.warnCouldNotRemove();
                        return;
                    }
                    view.removeUserBar(bar);
                    view.repaint();
                    view.revalidate();
                });

                bar.addSetOwnerButton(() -> {
                    String memberNickname = member.getNickname();
                    if (! bar.confirmSetOwner(memberNickname)) return;
                    if (! groupRepo.changeOwner(group, member)) {
                        bar.warnCouldNotSetOwner();
                        return;
                    }
                    group.setOwner(member);
                    view.close();
                    if (afterChangeOwner != null)
                        afterChangeOwner.run();
                });
            }

            UserBarController.setupUserBar(
                    bar, member, friends,
                    pendingFriendInvites, inviteRepo, userInSession
            );

        }
    }

    public void afterChangeOwner(Runnable action)
    {
        this.afterChangeOwner = action;
    }

    public void onClose(Runnable action)
    {
        this.view.onClose(action);
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }

}
