package controllers;

import gui.UserBar;
import gui.UserListFrame;
import models.User;
import models.group.Group;
import repositories.group.GroupInFileRepository;

import javax.swing.*;

public class GroupMemberListController {

    public GroupMemberListController(User user, Group group, JFrame frame, UserListFrame view)
    {
        var groupRepo = new GroupInFileRepository();

        var ownerBar = new UserBar(group.getOwner());
        var btOwner = new JButton("Owner");
        btOwner.setEnabled(false);
        ownerBar.addButton(btOwner);
        view.addUserBar(ownerBar);

        for (var member : group.getUsers()) {
            var bar = new UserBar(member);
            view.addUserBar(bar);
            if (user.equals(group.getOwner())) {
                JButton btRemove = new JButton("Remove");
                bar.addButton(btRemove);
                btRemove.addActionListener(evt -> {
                    group.removeUser(member);
                    groupRepo.updateGroup(group);
                    view.removeUserBar(bar);
                });

                JButton btSetOwner = new JButton("Set owner");
                bar.addButton(btSetOwner);
                btSetOwner.addActionListener(evt -> {
                    group.changeOwner(member);
                    groupRepo.updateGroup(group);
                    frame.dispose();
                });
            }
        }
    }

}
