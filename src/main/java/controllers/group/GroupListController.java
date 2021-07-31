package controllers.group;

import gui.group.GroupBar;
import gui.group.GroupListPanel;
import models.User;
import models.group.Group;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;

import javax.swing.*;

public class GroupListController
{
    private IGroupRepository groupRepo;
    private GroupListPanel view;
    private User user;

    public GroupListController(User user)
    {
        this.groupRepo = new GroupInFileRepository();
        this.user = user;
        this.view = new GroupListPanel();

        groupRepo.getGroups(user).forEach(this::addGroup);

        view.onClickNewGroup(() -> {
            var con = new GroupCreationController(user);
            con.onClose(() -> view.getNewGroupButton().setEnabled(true));
            con.onCreation(this::addGroup);
            con.display();
        });
    }

    private void addGroup(Group group)
    {
        var con = new GroupBarController(user, group);
        GroupBar bar = con.getBar();
        con.onDelete(() -> {
            view.remove(bar);
            if (!groupRepo.removeGroup(group.getId())) {
                // TODO dialog couldn't delete group
            }
        });
        view.add(bar);
    }


    public GroupListPanel getPanel()
    {
        return view;
    }
}
