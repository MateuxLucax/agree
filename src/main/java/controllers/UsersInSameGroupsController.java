package controllers;

import gui.UsersInSameGroupsFrame;
import models.User;
import models.UserToGroupsMap;
import repositories.group.GroupRepository;

import javax.swing.*;

public class UsersInSameGroupsController
{
    private final UsersInSameGroupsFrame view;

    public UsersInSameGroupsController(User userInSession)
    {
        view = new UsersInSameGroupsFrame();

        var groupRepo = new GroupRepository();

        var map = new UserToGroupsMap();
        for (var group : groupRepo.getGroups(userInSession)) {
            if (! group.ownedBy(userInSession))
                map.add(group.getOwner(), group);
            for (var member : groupRepo.getMembers(group))
                if (! member.equals(userInSession))
                    map.add(member, group);
        }

        for (var user : map.userSet()) {
            // TODO replace this JLabel with something better
            view.addUser(new JLabel(user.getNickname() + " belongs to " + map.get(user)));
        }
    }

    public void onClose(Runnable action)
    {
        view.onClose(action);
    }

    public void display()
    {
        view.display();
    }
}
