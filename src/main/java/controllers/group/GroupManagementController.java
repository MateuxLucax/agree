package controllers.group;

import gui.group.GroupManagementFrame;
import models.group.Group;
import repositories.group.GroupRepository;
import repositories.group.IGroupRepository;

import java.util.function.Consumer;

public class GroupManagementController {

    private final GroupManagementFrame view;
    private final IGroupRepository groupRepo;

    private Runnable afterDelete;
    private Consumer<Group> afterUpdate;

    public GroupManagementController(Group group)
    {
        this.groupRepo = new GroupRepository();
        view = new GroupManagementFrame(group);

        view.onClickDelete(() -> {
            if (! view.confirmDelete()) return;
            if (! groupRepo.deleteGroup(group.getId())) {
                view.warnCouldNotDelete();
                return;
            }
            if (afterDelete != null) afterDelete.run();
            view.close();
        });

        view.onClickSave(() -> {
            String newName    = view.getName();
            String newPicture = view.getPicture();

            if (newName.isEmpty()) {
                view.warnInvalidInput();
                return;
            }

            String oldName    = group.getName();
            String oldPicture = group.getPicture();

            group.setName(newName);
            group.setPicture(newPicture);

            if (! groupRepo.updateGroup(group)) {
                // restore previous field values
                group.setName(oldName);
                group.setPicture(oldPicture);
                view.warnCouldNotSave();
                return;
            }

            if (afterUpdate != null)
                afterUpdate.accept(group);

            view.close();
        });
    }

    public void afterDelete(Runnable action) {
        this.afterDelete = action;
    }

    public void afterUpdate(Consumer<Group> action) {
        this.afterUpdate = action;
    }

    public void onClose(Runnable action) {
        view.onClose(action);
    }

    public void display() {
        view.pack();
        view.setVisible(true);
    }

}
