package app;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.laf.DarculaThemeDarklafLookAndFeel;
import controllers.AuthController;
import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import gui.*;
import models.User;
import models.UserGroupsMap;
import models.group.Group;
import models.invite.FriendshipInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import models.invite.InviteState;
import models.message.Message;
import repositories.group.IGroupRepository;
import repositories.message.IMessageRepository;
import repositories.user.IUserRepository;
import services.login.ILoginService;
import services.login.LoginService;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Application {

    private UserSession session;
    private IMessageRepository msgRepo;
    private IGroupRepository groupRepo;
    private IUserRepository userRepo;

    public Application() {
        initializeTheme();
        startUserSession();
    }

    private void initializeTheme() {
        LafManager.install();
        LafManager.setTheme(new DarculaTheme());
        try {
            UIManager.setLookAndFeel(new DarculaThemeDarklafLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void startUserSession() {
        var authFrame = new JFrame();
        var authPanel = new AuthPanel();
        authFrame.setContentPane(authPanel.getJPanel());
        authFrame.pack();
        authFrame.setVisible(true);

        var authController = new AuthController(authFrame, authPanel);
        authController.onSuccess(user -> {
            session.initialize(user);
            initializeMainFrame();
        });

        // Setting up some shortcuts
        session   = UserSession.getInstance();
        msgRepo   = session.getMessageRepository();
        groupRepo = session.getGroupRepository();
        userRepo  = session.getUserRepository();
    }

    private void initializeMainFrame() {
        var mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        var mainPane = new JTabbedPane();
        var groupsPanel = new JPanel();
        mainPane.addTab("Groups", groupsPanel);
        {
            groupsPanel.setLayout(new BoxLayout(groupsPanel, BoxLayout.PAGE_AXIS));
            var btNewGroup = new JButton("+ New group");
            groupsPanel.add(btNewGroup);
            btNewGroup.addActionListener(evt -> {
                var groupCreationFrame = new JFrame();
                var groupCreationPanel = new CreateGroupPanel();
                groupCreationPanel.onCreation(groupName -> {
                    var group = new Group(groupName, session.getUser());
                    if (groupRepo.createGroup(group)) {
                        groupsPanel.add(createGroupBar(group, groupsPanel));
                        groupCreationFrame.dispose();
                    }
                });
                groupCreationFrame.setContentPane(groupCreationPanel.getJPanel());
                groupCreationFrame.pack();
                groupCreationFrame.setVisible(true);
            });

            List<Group> groups = groupRepo.getGroups(session.getUser());
            for (var group : groups)
                groupsPanel.add(createGroupBar(group, groupsPanel));
        }

        var friendsPanel = new JPanel();
        mainPane.addTab("Friends", friendsPanel);
        // TODO

        var morePanel = new JPanel();
        mainPane.addTab("More", morePanel);
        {
            var btSearchForUsers = new JButton("Search for users");
            morePanel.add(btSearchForUsers);
            btSearchForUsers.addActionListener(evt -> {
                btSearchForUsers.setEnabled(false);
                var searchFrame = new JFrame();
                searchFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        btSearchForUsers.setEnabled(true);
                        btSearchForUsers.repaint();
                        btSearchForUsers.revalidate();
                        searchFrame.dispose();
                    }
                });
                var searchPanel = new UserSearchPanel();
                searchFrame.setContentPane(searchPanel);
                searchPanel.onSearch(searchString -> {
                    List<User>    users = userRepo.searchUser(searchString);
                    List<UserBar> bars  = new ArrayList<>(users.size());
                    for (var user : users) {
                        var bar = new UserBar(user);

                        // If the user isn't in our friend list,
                        // we either have or haven't sent a friend request to him.
                        // If we have, we'll show the "invite sent" button, greyed-out.
                        // If we haven't, we'll show the "ask to be friends" button,
                        // which when pressed sends the request to the user and
                        // also gets updated to the "request sent" button.
                        if (!session.getUser().equals(user) && !session.getFriends().contains(user)) {
                            var btSent = new JButton("Request sent");
                            btSent.setEnabled(false);

                            boolean alreadySentRequest = false;
                            List<Invite> invs = session.getInvites();
                            for (int i = 0; !alreadySentRequest && i < invs.size(); i++) {
                                if (invs.get(i).to().equals(user))
                                    alreadySentRequest = true;
                            }
                            if (alreadySentRequest) {
                                bar.addButton(btSent);
                            } else {
                                var btAsk = new JButton("Ask to be friends");
                                bar.addButton(btAsk);
                                btAsk.addActionListener(evt0 -> {
                                    var invite = new FriendshipInvite(session.getUser(), user, InviteState.PENDING);
                                    session.getInviteRepository().addInvite(invite);
                                    bar.removeButton(btAsk);
                                    bar.addButton(btSent);
                                    bar.repaint();
                                    bar.revalidate();
                                });
                            }
                        }
                        bars.add(bar);
                    }
                    return bars;
                });
                searchFrame.pack();
                searchFrame.setVisible(true);
            });

            var btInvites = new JButton("Invites");
            morePanel.add(btInvites);
            btInvites.addActionListener(evt -> {
                btInvites.setEnabled(false);
                var invitesFrame = new JFrame();
                invitesFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        btInvites.setEnabled(true);
                        btInvites.repaint();
                        btInvites.revalidate();
                        invitesFrame.dispose();
                    }
                });
                var invitesPanel = new InviteListPanel();
                List<Invite> invites = session.getInviteRepository().getInvites(session.getUser());
                for (var inv : invites) {
                    var invBar = new InviteBar(inv, session.getUser());
                    invitesPanel.addInvite(invBar);
                    if (inv.getState() == InviteState.PENDING && inv.to().equals(session.getUser())) {
                        invBar.onAccept(() -> {
                            inv.setState(InviteState.ACCEPTED);
                            // TODO actually update invite in the database
                            if (inv instanceof FriendshipInvite) {
                                // TODO actually add friendship relationship in the database
                            } else {  // inv instanceof GroupInvite
                                // TODO actually add user to the group in the database
                            }
                        });
                        invBar.onDecline(() -> {
                            inv.setState(InviteState.DECLINED);
                            // TODO actually update invite in the database
                        });
                    }
                }
                invitesFrame.setContentPane(invitesPanel);
                invitesFrame.pack();
                invitesFrame.setVisible(true);
            });

            // usg: Users in the Same Group as you
            // (couldn't think of a more concise name, so I just made up an acronym to avoid really long variable names)
            var btUsg = new JButton("Users in the same groups as you");
            morePanel.add(btUsg);
            btUsg.addActionListener(evt -> {
                btUsg.setEnabled(false);
                var usgFrame = new JFrame();
                usgFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        btUsg.setEnabled(true);
                        btUsg.repaint();
                        btUsg.revalidate();
                        usgFrame.dispose();
                    }
                });
                var usgScrollPane = new JScrollPane();
                usgScrollPane.getVerticalScrollBar().setUnitIncrement(20);
                var usgPanel = new JPanel();
                usgScrollPane.setViewportView(usgPanel);
                usgPanel.setLayout(new BoxLayout(usgPanel, BoxLayout.PAGE_AXIS));
                var usgMap = new UserGroupsMap();
                for (var group : session.getGroups()) {
                    // Don't show the user in session himself in this list
                    if (group.isMember(session.getUser()))
                        continue;
                    usgMap.add(group.getOwner(), group);
                    for (var user : group.getUsers())
                        usgMap.add(user, group);
                }
                for (var user : usgMap.userSet()) {
                    List<Group> groups = usgMap.get(user);
                    // Show which groups the user has in common in a string formatted like
                    // user123 belongs to group1, The Group and final group
                    var strGroups = new StringBuilder();
                    strGroups.append(user.getNickname()).append(" belongs to ");
                    strGroups.append(groups);

                    usgPanel.add(new JLabel(strGroups.toString()));
                    // Just in a JLabel for now
                }
                usgFrame.setContentPane(usgScrollPane);
                usgFrame.pack();
                usgFrame.setVisible(true);
            });
        }
        mainFrame.setContentPane(mainPane);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public JPanel createGroupBar(Group group, JPanel groupsPanel) {
        var groupBar = new JPanel();
        var groupLabel = new JLabel(group.getName());
        groupBar.add(groupLabel);

        var btChat = new JButton("Chat");
        groupBar.add(btChat);
        btChat.addActionListener(evt -> {
            btChat.setEnabled(false);
            var chatFrame = new JFrame();
            chatFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    btChat.setEnabled(true);
                    btChat.repaint();
                    btChat.revalidate();
                    chatFrame.dispose();
                }
            });
            MessagingPanel chatPanel = createGroupMessagingPanel(group);
            chatFrame.setContentPane(chatPanel);
            chatFrame.pack();
            chatFrame.setVisible(true);
        });

        var btMembers = new JButton("Members");
        groupBar.add(btMembers);
        btMembers.addActionListener(evt -> {
            btMembers.setEnabled(false);
            var membersFrame = new JFrame();
            membersFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    btMembers.setEnabled(true);
                    btMembers.repaint();
                    btMembers.revalidate();
                    membersFrame.dispose();
                }
            });
            UserListPanel membersPanel = createMembersPanel(group, membersFrame);
            membersFrame.setContentPane(membersPanel);
            membersFrame.pack();
            membersFrame.setVisible(true);
        });

        var btInviteFriends = new JButton("Invite friends");
        groupBar.add(btInviteFriends);
        btInviteFriends.addActionListener(evt -> {
            btInviteFriends.setEnabled(false);
            var groupInviteFrame = new JFrame();
            groupInviteFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    btInviteFriends.setEnabled(true);
                    btInviteFriends.repaint();
                    btInviteFriends.revalidate();
                    groupInviteFrame.dispose();
                }
            });
            GroupInvitePanel groupInvitePanel = createGroupInvitePanel(group);
            groupInviteFrame.setContentPane(groupInvitePanel);
            groupInviteFrame.pack();
            groupInviteFrame.setVisible(true);
        });

        if (group.isOwnedBy(session.getUser())) {
            var btManage = new JButton("Manage");
            groupBar.add(btManage);
            btManage.addActionListener(evt -> {
                btManage.setEnabled(false);
                var manageFrame = new JFrame();
                manageFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        btManage.setEnabled(true);
                        btManage.repaint();
                        btManage.revalidate();
                        manageFrame.dispose();
                    }
                });
                var managePanel = new GroupManagementPanel(group.getName());
                managePanel.onRename(newName -> {
                    group.setName(newName);
                    if (groupRepo.updateGroup(group)) {
                        groupLabel.setText(newName);
                        groupBar.repaint();
                        groupBar.revalidate();
                    }
                    btManage.setEnabled(true);
                    manageFrame.dispose();
                });
                managePanel.onDelete(() -> {
                    groupRepo.removeGroup(group.getId());
                    groupsPanel.remove(groupBar);
                    manageFrame.dispose();
                });
                manageFrame.setContentPane(managePanel.getJPanel());
                manageFrame.pack();
                manageFrame.setVisible(true);
            });
        }
        return groupBar;
    }

    public MessagingPanel createGroupMessagingPanel(Group group) {
        var msgPanel = new MessagingPanel();
        msgPanel.loadMessages(group.getMessages());
        msgPanel.onLoadOlder(() -> {
            msgRepo.getMessagesBefore(group, group.oldestMessageDate());
            msgPanel.loadMessages(group.getMessages());
        });
        msgPanel.onLoadNewer(() -> {
            msgRepo.getMessagesAfter(group, group.newestMessageDate());
            msgPanel.loadMessages(group.getMessages());
        });
        msgPanel.onSendMessage(text -> {
            var msg = new Message(session.getUser(), text, new Date());
            boolean ok = msgRepo.addMessage(group, msg);
            if (ok) {
                group.loadMessageBelow(msg);
                msgPanel.loadMessages(group.getMessages());
            }
            return ok;
        });
        return msgPanel;
    }

    public UserListPanel createMembersPanel(Group group, JFrame membersFrame) {
        var membersPanel = new UserListPanel();

        var ownerBar = new UserBar(group.getOwner());
        var btOwner = new JButton("Owner");
        btOwner.setEnabled(false);
        ownerBar.addButton(btOwner);
        membersPanel.addUserBar(ownerBar);

        for (var user : group.getUsers()) {
            var bar = new UserBar(user);
            if (session.getUser().equals(group.getOwner())) {
                JButton btRemove = new JButton("Remove");
                bar.addButton(btRemove);
                btRemove.addActionListener(evt -> {
                    group.removeUser(user);
                    groupRepo.updateGroup(group);
                    membersPanel.removeUserBar(bar);
                });

                JButton btSetOwner = new JButton("Set owner");
                bar.addButton(btSetOwner);
                btSetOwner.addActionListener(evt -> {
                    group.changeOwner(user);
                    groupRepo.updateGroup(group);
                    membersFrame.dispose();
                });
            }
            membersPanel.addUserBar(bar);
        }
        return membersPanel;
    }

    public GroupInvitePanel createGroupInvitePanel(Group group) {
        var panel = new GroupInvitePanel();
        List<User> friends = session.getFriendshipRepository().getFriends(session.getUser());
        for (var friend : friends) {
            // Don't show friends who already are in the group
            if (group.isMember(friend))
                continue;
            // Check whether we've already invited this friend to this group
            boolean alreadySentInvite = false;
            List<Invite> invs = session.getInviteRepository().getInvites(session.getUser());
            for (int i = 0; !alreadySentInvite && i < invs.size(); i++) {
                Invite inv = invs.get(i);
                if (inv instanceof GroupInvite && inv.to().equals(friend))
                    alreadySentInvite = true;
            }
            // If we've already sent a group invite to this friend,
            // then don't show the "Invite to group" button, but rather
            // the greyed-out "Invite sent" button
            var bar = new UserBar(friend);
            panel.addBar(bar);
            var btSent = new JButton("Invite sent");
            btSent.setEnabled(false);
            if (alreadySentInvite) {
                bar.addButton(btSent);
            } else {
                var btn = new JButton("Invite to group");
                bar.addButton(btn);
                btn.addActionListener(evt -> {
                    var invite = new GroupInvite(session.getUser(), friend, InviteState.PENDING, group);
                    session.getInvites().add(invite);
                    // TODO actually add invite to the database
                    var invBar = new InviteBar(invite, session.getUser());
                    bar.removeButton(btn);
                    bar.repaint();
                    bar.addButton(btSent);
                });
            }
        }
        return panel;
    }

    public static void main(String[] args) {
        new Application();
    }
}
