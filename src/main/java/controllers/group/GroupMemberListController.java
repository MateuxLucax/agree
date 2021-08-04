package controllers.group;

import controllers.UserBarController;
import gui.UserListFrame;
import gui.group.MemberBar;
import models.User;
import models.group.Group;
import models.invite.InviteState;
import repositories.friendship.FriendshipRepository;
import repositories.group.GroupRepository;
import repositories.group.IGroupRepository;
import repositories.invite.InviteRepositoryInFile;

public class GroupMemberListController {

    private final UserListFrame view;
    private final IGroupRepository groupRepo;
    private Runnable onChangeOwner;

    public GroupMemberListController(User userInSession, Group group)
    {
        view = new UserListFrame();
        groupRepo = new GroupRepository();

        var inviteRepo = new InviteRepositoryInFile();

        var friends = (new FriendshipRepository()).getFriends(userInSession);
        var pendingFriendInvites = inviteRepo.getFriendInvites(userInSession, InviteState.PENDING);

        var owner = group.getOwner();
        var ownerBar = new MemberBar(owner.getNickname());
        view.addUserBar(ownerBar);
        ownerBar.showOwnerButton();
        UserBarController.setupUserBar(
                userInSession, owner,
                friends, pendingFriendInvites,
                inviteRepo, ownerBar
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
                    userInSession, member,
                    friends, pendingFriendInvites,
                    inviteRepo, bar
            );

        }
    }

    private void updateOwnership(Group group, User oldOwner, User newOwner)
    {
        // Early returns instead of deeply nested ifs and elses

        group.setOwner(newOwner);
        if (!groupRepo.updateGroup(group)) {
            // Restore old owner
            group.setOwner(oldOwner);
            // TODO dialog "could not set user as owner"
            return;
        }

        // Make the old owner a regular member
        if (!groupRepo.addMember(group, oldOwner)) {
            // Restore old owner
            group.setOwner(oldOwner);
            // Also in the database, since the updateGroup changing the owner was successful
            groupRepo.updateGroup(group);
            // TODO dialog "could not set user as owner"
            return;
        }

        // The new owner is not a member anymore -- it's an owner
        if (!groupRepo.removeMember(group, newOwner)) {
            // Restore old owner
            group.setOwner(oldOwner);
            // Also in the database, since the updateGroup changing the owner was successful
            groupRepo.updateGroup(group);
            // Also remove the old owner as member, since the addMember above was successful
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
