package controllers.group;

import gui.group.GroupCreationFrame;
import models.User;
import models.group.Group;
import repositories.group.GroupRepository;

import java.util.function.Consumer;

public class GroupCreationController
{
    private final GroupCreationFrame view;
    private Consumer<Group> afterCreation;

    public GroupCreationController(User user) {
        var groupRepo = new GroupRepository();
        this.view = new GroupCreationFrame();
        view.onClickCreate(groupName -> {
            var group = new Group(groupName, user);
            if (! groupRepo.createGroup(group)) {
                view.warnCouldNotCreate();
                return;
            }
            view.close();
            if (afterCreation != null) afterCreation.accept(group);
        });
    }

    public void onClose(Runnable action) {
        this.view.onClose(action);
    }

    public void afterCreation(Consumer<Group> action) {
        this.afterCreation = action;
    }

    public void display() {
        view.pack();
        view.setVisible(true);
    }
}
