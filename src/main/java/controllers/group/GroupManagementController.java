package controllers.group;

import gui.group.GroupManagementFrame;
import models.group.Group;
import repositories.group.GroupRepository;
import repositories.group.IGroupRepository;

import java.util.function.Consumer;

public class GroupManagementController {

    private final Group group;
    private final GroupManagementFrame view;
    private final IGroupRepository groupRepo;
    private Consumer<String> onRename;
    private Runnable onDelete;

    public GroupManagementController(Group model)
    {
        this.groupRepo = new GroupRepository();
        this.group = model;
        view = new GroupManagementFrame(model.getName());

        view.onClickDelete(() -> {
            groupRepo.removeGroup(group.getId());
            if (this.onDelete != null)
                this.onDelete.run();
            view.close();
        });

        view.onClickRename(newName -> {
            String oldName = group.getName();
            group.setName(newName);
            if (groupRepo.renameGroup(group) && this.onRename != null)
                this.onRename.accept(newName);
            else  // renaming failed
                group.setName(oldName);
            view.close();
        });
    }

    public void onClose(Runnable action)
    {
        this.view.onClose(action);
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
