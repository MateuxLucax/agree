package controllers;

import gui.GroupBar;
import gui.GroupCreationFrame;
import gui.GroupListPanel;
import models.User;
import models.group.Group;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GroupListController
{
    private Map<Group, GroupBar> groupToBarMap;
    private IGroupRepository groupRepo;
    private GroupListPanel view;
    private User user;

    public GroupListController(User user)
    {
        this.groupToBarMap = new HashMap<>();
        this.groupRepo = new GroupInFileRepository();
        this.user = user;
        this.view = new GroupListPanel();

        groupRepo.getGroups(user).forEach(this::addGroup);

        view.onClickNewGroup(() -> {
            // TODO make a GroupCreationController
            var groupCreationFrame = new GroupCreationFrame(view.getNewGroupButton());
            groupCreationFrame.onCreation(groupName -> {
                var group = new Group(groupName, user);
                if (groupRepo.createGroup(group))
                    addGroup(group);
            });
            groupCreationFrame.pack();
            groupCreationFrame.setVisible(true);
        });
    }

    private void addGroup(Group group)
    {
        var con = new GroupBarController(user, group);
        GroupBar bar = con.getBar();

        con.onDelete(() -> {
            view.remove(bar);
            groupRepo.removeGroup(group.getId());
            groupToBarMap.remove(group);
        });

        groupToBarMap.put(group, bar);
        view.add(bar);
    }


    public JPanel getPanel()
    {
        return view;
    }
}
