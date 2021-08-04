package controllers.group;

import gui.group.GroupInviteFrame;
import models.User;
import models.group.Group;
import models.invite.GroupInvite;
import models.invite.Invite;
import repositories.friendship.FriendshipRepository;
import repositories.group.GroupRepository;
import repositories.invite.InviteRepositoryInFile;

import javax.swing.*;
import java.util.List;

public class GroupInviteFriendsController {

    private GroupInviteFrame view;

    public GroupInviteFriendsController(User user, Group group)
    {
        view = new GroupInviteFrame();

        var friendRepo = new FriendshipRepository();
        var inviteRepo = new InviteRepositoryInFile();
        var groupRepo  = new GroupRepository();

        List<User>   friends = friendRepo.getFriends(user);
        List<Invite> invites = inviteRepo.getInvites(user);
        List<User>   members = groupRepo.getMembers(group);

        var btSent = new JButton("Invite sent");
        btSent.setEnabled(false);

        for (var friend : friends) {
            // Don't show friends who are already in the group
            if (members.contains(friend))
                continue;

            boolean alreadySentInvite = false;
            for (var inv : invites) {
                alreadySentInvite = (inv instanceof GroupInvite) && inv.to(friend);
                if (alreadySentInvite)
                    break;
            }

            /* TODO use a InviteableFriendBar and InviteableFriendBarController instead of UserBar
               (I'm not a fan of long names, but just FriendBar is not enough here, since
                we're specifically in the context of a group to which we want to invite friends)
            var bar = new UserBar(friend);
            view.addFriendBar(bar);

            if (alreadySentInvite) {
                bar.addButton(btSent);
            } else {
                var btn = new JButton("Invite to group");
                bar.addButton(btn);
                btn.addActionListener(evt -> {
                    var invite = new GroupInvite(user, friend, InviteState.PENDING, group);
                    inviteRepo.addInvite(invite);
                    bar.removeButton(btn);
                    bar.repaint();
                    bar.addButton(btSent);
                });
            }
             */
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
