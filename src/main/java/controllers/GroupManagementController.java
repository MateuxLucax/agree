package controllers;

import gui.GroupManagementFrame;
import gui.GroupManagementPanel;
import models.group.Group;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;

import javax.swing.*;

public class GroupManagementController {

    private Group group;
    private GroupManagementFrame view;
    private IGroupRepository groupRepo;

    public GroupManagementController(Group model, JButton btnThatOpenedTheFrame)
    {
        this.groupRepo = new GroupInFileRepository();
        this.group = model;
        view = new GroupManagementFrame(model.getName(), btnThatOpenedTheFrame);
        view.onDelete(this::delete);
        view.onRename(this::rename);
    }

    // TODO update the panel listing the groups to rename and delete group bars accordingly

    public void rename(String newName)
    {
        group.setName(newName);
        if (groupRepo.updateGroup(group)) {
            // TODO update corresponding group bar (all the commented stuff is how that was previously done)
            //   maybe by taking a GroupListController,
            //   which has a list of GroupBarControllers,
            //   (in a Map<Group,GroupBarController>?)
            //   so I can do something like
            //   :
            //   GroupListController groupListController; (taken as parameter in the constructor?)
            //   GroupBarController bar = groupListController.getGroupBarController(this.group);
            //   bar.rename(newName);
            //   bar.enableManageButton();
            //
            // previous implementation:
            // groupLabel.setText(newName);
            // groupBar.repaint();
            // groupBar.revalidate();
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
