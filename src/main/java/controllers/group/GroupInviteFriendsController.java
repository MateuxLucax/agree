package controllers.group;

import gui.group.GroupInviteFrame;
import gui.group.InviteableFriendBar;
import models.User;
import models.group.Group;
import models.invite.GroupInvite;
import repositories.friendship.FriendshipRepository;
import repositories.group.GroupRepository;
import repositories.invite.InviteRepository;

import javax.swing.*;
import java.util.List;

public class GroupInviteFriendsController {

    private final GroupInviteFrame view;

    public GroupInviteFriendsController(User user, Group group)
    {
        view = new GroupInviteFrame();

        var friendRepo = new FriendshipRepository();
        var inviteRepo = new InviteRepository();
        var groupRepo  = new GroupRepository();

        List<User>        friends   = friendRepo.getFriends(user);
        List<GroupInvite> groupInvs = inviteRepo.getGroupInvites(user);
        List<User>        members   = groupRepo.getMembers(group);

        var btSent = new JButton("Invite sent");
        btSent.setEnabled(false);

        for (var friend : friends) {
            // Don't show friends who are already in the group
            if (members.contains(friend))
                continue;

            var bar = new InviteableFriendBar(friend.getNickname());
            view.addFriendBar(bar);
            if (groupInvs.stream().anyMatch(inv -> inv.to(friend))) {
                bar.showGroupInviteSentButton();
            } else {
                bar.showInviteToGroupButton();
                bar.onClickInviteToGroup(() -> {
                    var inv = new GroupInvite(user, friend, group);
                    if (inviteRepo.addInvite(inv)) {
                        // TODO dialog "invite sent successfully"
                        bar.replaceWithGroupInviteSentButton();
                    } // TODO else dialog "couldn't send the invite"
                });
            }
        }
    }

    public void onClose(Runnable action)
    {
        view.onClose(action);
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }
}
