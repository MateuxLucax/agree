package repositories.group;

import models.User;
import models.group.Group;
import repositories.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupRepository implements IGroupRepository
{
    private final Connection con;

    public GroupRepository()
    {
        con = DBConnection.get();
    }

    @Override
    public boolean createGroup(Group group) {
        var sql = "INSERT INTO groups (id, ownerNickname, name) VALUES (?, ?, ?)";
        var uuid = UUID.randomUUID().toString();
        group.setId(uuid);
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, uuid);
            pstmt.setString(2, group.getOwner().getNickname());
            pstmt.setString(3, group.getName());
            pstmt.execute();
            return pstmt.getUpdateCount() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeGroup(String id) {
        var sql = "DELETE FROM groups g WHERE g.id = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.execute();
            return pstmt.getUpdateCount() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateGroup(Group group) {
        var sql = "UPDATE groups SET ownerNickname = ?, name = ? WHERE id = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, group.getOwner().getNickname());
            pstmt.setString(2, group.getName());
            pstmt.setString(3, group.getId());
            pstmt.execute();
            return pstmt.getUpdateCount() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /* The groups will not yet have members and messages loaded in them */
    @Override
    public List<Group> getGroups(User user) {
        var groups = new ArrayList<Group>();
        var sql = "SELECT g.id, g.name, g.ownerNickname " +
                  "FROM groups g WHERE g.ownerNickname = ? " +
                  "UNION " +
                  "SELECT g2.id, g2.name, g2.ownerNickname " +
                  "FROM groups g2, groupMembership m " +
                  "WHERE g2.id = m.groupId " +
                  "AND m.userNickname = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getNickname());
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                var id            = res.getString(1);
                var name          = res.getString(2);
                var ownerNickname = res.getString(3);
                var group = new Group(name, new User(ownerNickname));
                group.setId(id);
                groups.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public Group getGroup(String id) {
        var sql = "SELECT g,name, g.ownerNickname FROM groups g WHERE g.id = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet res = pstmt.executeQuery();
            if (!res.next())
                return null;
            var name          = res.getString(1);
            var ownerNickname = res.getString(2);
            return new Group(name, new User(ownerNickname));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public List<User> getMembers(Group group) {
        var users = new ArrayList<User>();
        var sql = "SELECT m.userNickname FROM groupMembership m WHERE m.groupId = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, group.getId());
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                users.add(new User(res.getString(1)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return users;
    }

    public boolean addMember(Group group, User member) {
        var sql = "INSERT INTO groupMembership (groupId, userNickname) VALUES (?, ?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, group.getId());
            pstmt.setString(2, member.getNickname());
            pstmt.execute();
            return pstmt.getUpdateCount() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeMember(Group group, User member) {
        var sql = "DELETE FROM groupMembership WHERE groupId = ? AND userNickname = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, group.getId());
            pstmt.setString(2, member.getNickname());
            pstmt.execute();
            return pstmt.getUpdateCount() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
