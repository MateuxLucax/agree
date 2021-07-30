package controllers.group;

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
    private Runnable onDelete;

    public GroupManagementController(Group model, JButton btnThatOpenedTheFrame)
    {
        this.groupRepo = new GroupInFileRepository();
        this.group = model;
        view = new GroupManagementFrame(model.getName(), btnThatOpenedTheFrame);

        view.onClickDelete(() -> {
            groupRepo.removeGroup(group.getId());
            if (this.onDelete != null)
                onDelete.run();
            view.dispose();
        });

        view.onClickRename(newName -> {
            String oldName = group.getName();
            group.setName(newName);
            if (groupRepo.updateGroup(group) && this.onRename != null)
                this.onRename.accept(newName);
            else  // renaming failed
                group.setName(oldName);
            view.dispose();
        });
    }

    public void onRename(Consumer<String> action)
    {
        this.onRename = action;
    }

    public void onDelete(Runnable action)
    {
        this.onDelete = action;
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }

}
