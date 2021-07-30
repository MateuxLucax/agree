package controllers;

import gui.GroupBar;
import gui.GroupCreationFrame;
import gui.GroupListPanel;
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
            var con = new GroupCreationController(view.getNewGroupButton(), user);
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
            groupRepo.removeGroup(group.getId());
        });
        view.add(bar);
    }


    public JPanel getPanel()
    {
        return view;
    }
}
