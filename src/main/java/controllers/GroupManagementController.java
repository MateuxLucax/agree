package controllers;

import gui.GroupManagementFrame;
import models.group.Group;
import repositories.group.GroupInFileRepository;
import repositories.group.IGroupRepository;

import javax.swing.*;
import java.util.function.Consumer;

public class GroupManagementController {

    private Group group;
    private GroupManagementFrame view;
    private IGroupRepository groupRepo;
    private Consumer<String> onRename;

    public GroupManagementController(Group model, GroupListController listCon, JButton btnThatOpenedTheFrame)
    {
        this.groupRepo = new GroupInFileRepository();
        this.group = model;
        view = new GroupManagementFrame(model.getName(), btnThatOpenedTheFrame);

        view.onDelete(() -> {
            groupRepo.removeGroup(group.getId());
            listCon.removeGroup(group);
            view.dispose();
        });

        view.onRename(newName -> {
            group.setName(newName);
            if (groupRepo.updateGroup(group))
                onRename.accept(newName);
            view.dispose();
        });
    }

    public void onRename(Consumer<String> action)
    {
        this.onRename = action;
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }

}
