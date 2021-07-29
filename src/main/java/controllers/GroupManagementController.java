package controllers;

import gui.GroupBarController;
import gui.GroupManagementFrame;
import gui.GroupManagementPanel;
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

    public GroupManagementController(Group model, GroupBarController barCon, JButton btnThatOpenedTheFrame)
    {
        this.groupRepo = new GroupInFileRepository();
        this.group = model;
        this.barCon = barCon;
        view = new GroupManagementFrame(model.getName(), btnThatOpenedTheFrame);
        view.onDelete(this::delete);
        view.onRename(this::rename);
    }

    // TODO update the panel listing the groups to rename and delete group bars accordingly

    public void rename(String newName)
    {
        group.setName(newName);
        if (groupRepo.updateGroup(group)) {
            barCon.rename(newName);
            barCon.reload();
        }
        // btManage.setEnabled(true);
        view.dispose();
    }

    public void delete()
    {
        groupRepo.removeGroup(group.getId());
        // TODO GroupBarController barController;
        //   GroupListController groupListController; (taken as parameter in the constructor?)
        //   groupListController.remove(this.group);
        //
        // previous implementation:
        // groupsPanel.remove(groupBar);
        view.dispose();
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }

}
