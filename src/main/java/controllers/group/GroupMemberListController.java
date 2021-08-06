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
    private Runnable onChangeOwner;

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
        ownerBar.showOwnerButton();
        UserBarController.setupUserBar(
                ownerBar, owner, friends,
                pendingFriendInvites, inviteRepo, userInSession
        );

        for (var member : groupRepo.getMembers(group)) {
            var bar = new MemberBar(member.getNickname());
            view.addUserBar(bar);
            if (group.ownedBy(userInSession)) {
                bar.showRemoveButton();
                bar.onClickRemove(() -> {
                    if (groupRepo.removeMember(group, member)) {
                        view.removeUserBar(bar);
                        view.repaint();
                        view.revalidate();
                    }
                    // TODO else dialog "could not remove member"
                });
                bar.showSetOwnerButton();
                bar.onClickSetOwner(() -> updateOwnership(group, owner, member));
            }
            UserBarController.setupUserBar(
                    bar, member, friends,
                    pendingFriendInvites, inviteRepo, userInSession
            );

        }
    }

    private void updateOwnership(Group group, User oldOwner, User newOwner)
    {
        // What this method does is essentially
        // group.setOwner(newOwner);
        // groupRepo.updateGroup(group);
        // groupRepo.addMember(group, oldOwner);
        // groupRepo.removeMember(group, newOwner);
        // but with corrections for each possible failure,
        // undoing the previous actions -- not leaving the ownership update half-done

        group.setOwner(newOwner);
        if (! groupRepo.updateGroup(group)) {
            group.setOwner(oldOwner);
            // TODO dialog "could not set user as owner"
            return;
        }

        // Make the old owner a regular member
        if (! groupRepo.addMember(group, oldOwner)) {
            group.setOwner(oldOwner);
            groupRepo.updateGroup(group);
            // TODO dialog "could not set user as owner"
            return;
        }

        // The new owner is not a member anymore -- it's an owner
        if (! groupRepo.removeMember(group, newOwner)) {
            group.setOwner(oldOwner);
            groupRepo.updateGroup(group);
            groupRepo.removeMember(group, oldOwner);
            // TODO dialog "could not set user as owner"
            return;
        }

        // At this point everything database-related involved in updating
        // the group ownership was done successfully
        if (onChangeOwner != null)
            onChangeOwner.run();
        view.close();
    }

    public void onChangeOwner(Runnable action)
    {
        this.onChangeOwner = action;
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
