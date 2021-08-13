package controllers.group;

import gui.group.GroupBar;
import gui.group.GroupListPanel;
import models.User;
import models.group.Group;
import repositories.group.GroupRepository;
import repositories.group.IGroupRepository;


public class GroupListController {

    private IGroupRepository groupRepo;
    private GroupListPanel view;
    private User user;

    public GroupListController(User user) {
        this.groupRepo = new GroupRepository();
        this.user      = user;
        this.view      = new GroupListPanel();

        view.onClickNewGroup(() -> {
            view.newGroupButtonSetEnabled(false);
            var con = new GroupCreationController(user);
            con.onClose(() -> view.newGroupButtonSetEnabled(true));
            con.onCreation(this::addGroup);
            con.display();
        });

        view.onClickRefresh(() -> {
            view.clear();
            loadGroupBars();
        });

        loadGroupBars();
    }

    private void loadGroupBars() {
        groupRepo.getGroups(user).forEach(this::addGroup);
    }

    private void addGroup(Group group) {
        var con = new GroupBarController(user, group);
        GroupBar bar = con.getBar();
        view.addGroupBar(bar);

        con.onDelete(() -> view.removeGroupBar(bar));
        con.onQuit(() -> view.removeGroupBar(bar));

    }

    public GroupListPanel getPanel() {
        return view;
    }
}
