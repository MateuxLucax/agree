package repositories.invite;

import models.User;
import models.group.Group;
import models.invite.FriendInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import repositories.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InviteRepository implements IInviteRepository
{
    private Connection con;

    public InviteRepository() {
        con = DBConnection.get();
    }

    @Override
    public List<FriendInvite> getFriendInvites(User user) {
        var invs = new ArrayList<FriendInvite>();
        var sql = "SELECT nicknameFrom, nicknameTo " +
                  "FROM FriendInvites " +
                  "WHERE nicknameFrom = ? OR nicknameTo = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getNickname());
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                var from  = new User(res.getString(1));
                var to    = new User(res.getString(2));
                invs.add(new FriendInvite(from, to));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return invs;
    }

    @Override
    public List<GroupInvite> getGroupInvites(User user) {
        var invs = new ArrayList<GroupInvite>();
        var sql = "SELECT i.nicknameFrom, i.nicknameTo, g.id, g.name, g.ownerNickname " +
                  "FROM   GroupInvites i, Groups g " +
                  "WHERE  i.groupId = g.id " +
                  "AND    (nicknameFrom = ? OR nicknameTo = ?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getNickname());
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                var from = new User(res.getString(1));
                var to = new User(res.getString(2));
                var groupId = res.getString(3);
                var groupName = res.getString(4);
                var owner = new User(res.getString(5));
                var group = new Group(groupName, owner);
                group.setId(groupId);
                invs.add(new GroupInvite(from, to, group));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return invs;
    }


    @Override
    public List<Invite> getInvites(User user) {
        var friendInvites = getFriendInvites(user);
        if (friendInvites == null)
            friendInvites = Collections.emptyList();
        var groupInvites  = getGroupInvites(user);
        if (groupInvites == null)
            groupInvites = Collections.emptyList();
        var invites = new ArrayList<Invite>(friendInvites.size() + groupInvites.size());
        invites.addAll(friendInvites);
        invites.addAll(groupInvites);
        return invites;
    }

    private boolean addFriendInvite(FriendInvite inv) {
        var sql = "INSERT INTO FriendInvites (nicknameFrom, nicknameTo) VALUES (?, ?)";
        try {
            var stmt = con.prepareStatement(sql);
            stmt.setString(1, inv.from().getNickname());
            stmt.setString(2, inv.to().getNickname());
            stmt.execute();
            return stmt.getUpdateCount() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean addGroupInvite(GroupInvite inv) {
        var sql = "INSERT INTO GroupInvites (nicknameFrom, nicknameTo, groupId) VALUES (?, ?, ?)";
        try {
            var stmt = con.prepareStatement(sql);
            stmt.setString(1, inv.from().getNickname());
            stmt.setString(2, inv.to().getNickname());
            stmt.setString(3, inv.getGroup().getId());
            stmt.execute();
            return stmt.getUpdateCount() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addInvite(Invite invite) {
        return invite instanceof GroupInvite
             ? addGroupInvite((GroupInvite) invite)
             : addFriendInvite((FriendInvite) invite);
    }

    private boolean removeGroupInvite(GroupInvite invite) {
        return false;  // TODO
    }

    private boolean removeFriendInvite(FriendInvite invite) {
        return false;  // TODO
    }

    @Override
    public boolean removeInvite(Invite invite) {
        return invite instanceof GroupInvite ?
                removeGroupInvite((GroupInvite) invite) :
                removeFriendInvite((FriendInvite) invite);
    }

}
