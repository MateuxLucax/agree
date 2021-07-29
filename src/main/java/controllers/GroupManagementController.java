package controllers;

import gui.GroupManagementFrame;
import models.group.Group;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;

import javax.swing.*;

public class GroupManagementController {

    private Group group;
    // barCon is the group bar of this group
    private GroupBarController barCon;
    private GroupManagementFrame view;
    private IGroupRepository groupRepo;

    public GroupManagementController(Group model, GroupBarController barCon, GroupListController listCon, JButton btnThatOpenedTheFrame)
    {
        this.groupRepo = new GroupInFileRepository();
        this.group = model;
        this.barCon = barCon;
        view = new GroupManagementFrame(model.getName(), btnThatOpenedTheFrame);

        view.onDelete(() -> {
            groupRepo.removeGroup(group.getId());
            listCon.removeGroup(group);
            view.dispose();
        });

        view.onRename(newName -> {
            group.setName(newName);
            if (groupRepo.updateGroup(group)) {
                barCon.rename(newName);
                barCon.reload();
            }
            view.dispose();
        });
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }

}
