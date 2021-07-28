package controllers;

import gui.GroupInviteFrame;
import gui.UserBar;
import models.User;
import models.group.Group;
import models.invite.GroupInvite;
import models.invite.Invite;
import models.invite.InviteState;
import repositories.friendship.FriendshipInFileRepository;
import repositories.invite.InviteRepositoryInFile;

import javax.swing.*;
import java.util.List;

public class GroupInviteController {

    public GroupInviteController(User user, Group group, GroupInviteFrame view)
    {
        var friendRepo = new FriendshipInFileRepository();
        var inviteRepo = new InviteRepositoryInFile();

        List<User> friends = friendRepo.getFriends(user);
        List<Invite> invitesSent = inviteRepo.getInvites(user);

        var btSent = new JButton("Invite sent");
        btSent.setEnabled(false);

        for (var friend : friends) {
            // Don't show friends who are already in the group
            if (group.isMember(friend))
                continue;

            boolean alreadySentInvite = false;
            for (var inv : invitesSent) {
                alreadySentInvite = (inv instanceof GroupInvite) && inv.to().equals(friend);
                if (alreadySentInvite)
                    break;
            }

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
        }
    }
}
