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

    private Runnable afterDelete;
    private Consumer<String> afterRename;

    public GroupManagementController(Group model)
    {
        this.groupRepo = new GroupRepository();
        this.group = model;
        view = new GroupManagementFrame(model.getName());

        view.onClickDelete(() -> {
            if (! groupRepo.deleteGroup(group.getId())) {
                // TODO dialog "couldn't delete group"
                return;
            }
            if (afterDelete != null) afterDelete.run();
            view.close();
        });

        view.onClickRename(newName -> {
            String oldName = group.getName();
            group.setName(newName);
            if (! groupRepo.renameGroup(group)) {
                group.setName(oldName);  // restore previous name
                // TODO dialog "couldn't rename group"
                return;
            }
            if (afterRename != null) afterRename.accept(newName);
            view.close();
        });
    }

    public void afterDelete(Runnable action) {
        this.afterDelete = action;
    }

    public void afterRename(Consumer<String> action) {
        this.afterRename = action;
    }

    public void onClose(Runnable action) {
        view.onClose(action);
    }

    public void display() {
        view.pack();
        view.setVisible(true);
    }

}
