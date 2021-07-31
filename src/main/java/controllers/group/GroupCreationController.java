package controllers.group;

import gui.group.GroupCreationFrame;
import models.User;
import models.group.Group;
import repositories.group.GroupInFileRepository;

import javax.swing.*;
import java.util.function.Consumer;

public class GroupCreationController
{
    private GroupCreationFrame view;
    private Consumer<Group> onCreation;

    public GroupCreationController(User user)
    {
        var groupRepo = new GroupInFileRepository();
        this.view = new GroupCreationFrame();
        view.onCreation(groupName -> {
            var group = new Group(groupName, user);
            if (groupRepo.createGroup(group) && this.onCreation != null)
                this.onCreation.accept(group);
        });
    }

    public void onClose(Runnable action)
    {
        this.view.onClose(action);
    }

    public void onCreation(Consumer<Group> action)
    {
        this.onCreation = action;
    }

    public void display()
    {
        view.pack();
        view.setVisible(true);
    }
}
