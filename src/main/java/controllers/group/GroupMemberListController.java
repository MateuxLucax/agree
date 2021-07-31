package controllers.group;

import gui.UserListFrame;
import models.User;
import models.group.Group;

public class GroupMemberListController {

    private UserListFrame view;

    public GroupMemberListController(User user, Group group)
    {
        view = new UserListFrame();

        /* TODO use a MemberBar and MemberBarController instead of UserBar
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
                    view.dispose();
                });
            }
        }
         */
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
