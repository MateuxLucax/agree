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
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, group.getOwner().getNickname());
            pstmt.setString(3, group.getName());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeGroup(String id) {
        var sql = "DELETE FROM groups g WHERE g.id = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateGroup(Group group) {
        var sql = "UPDATE groups SET ownerNickname = ?, name = ? WHERE id = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, group.getOwner().getNickname());
            pstmt.setString(2, group.getName());
            pstmt.setString(3, group.getId());
            return pstmt.executeUpdate() == 1;
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
        try (var pstmt = con.prepareStatement(sql)) {
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
        try (var pstmt = con.prepareStatement(sql)) {
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

    @Override
    public List<User> getMembers(Group group) {
        var users = new ArrayList<User>();
        var sql = "SELECT m.userNickname FROM groupMembership m WHERE m.groupId = ?";
        try (var pstmt = con.prepareStatement(sql)) {
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

    /**
     * Change the owner of group to newOwner in the database.
     * This involves not only updating the ownerNickname field in the Groups table,
     * but also making the old owner a member and removing the new owner from the members.
     * Because the owner is not part of the members.
     * Note: it doesn't call group.setOwner(newOwner), but depends on group.getOwner()
     * to know who the old owner is.
     */
    @Override
    public boolean changeOwner(Group group, User newOwner) {
        // This is a transaction: all actions have to be performed successfully,
        // otherwise the database will be in an invalid or wrong state
        // (e.g. new owner is still a member, old owner didn't become a member
        // and thus can't open the group anymore...)
        // and that's why we use con.setAutoCommit(false), perform each statement,
        // then call con.commit().
        // If something goes wrong, we call con.rollback().
        // At the end, we set con.autoCommit(true) again.

        var sql1 = "UPDATE Groups SET ownerNickname = ? WHERE id = ?";
        var sql2 = "UPDATE GroupMembership SET userNickname = ? WHERE userNickname = ? AND groupId = ?";
        // sql2 replaces (newOwner, id) with (oldOwner, id) in GroupMembership,
        // simultaneously removing the newOwner from the members and adding the oldOwner.

        try (var pstmt1 = con.prepareStatement(sql1);
             var pstmt2 = con.prepareStatement(sql2))
        {
            con.setAutoCommit(false);

            String groupId = group.getId();
            String newNick = newOwner.getNickname();
            String oldNick = group.getOwner().getNickname();

            pstmt1.setString(1, newNick);
            pstmt1.setString(2, groupId);
            int rowCount1 = pstmt1.executeUpdate();

            System.out.println(pstmt1);
            System.out.println(rowCount1 + " rows matched");

            pstmt2.setString(1, oldNick);
            pstmt2.setString(2, newNick);
            pstmt2.setString(3, groupId);
            int rowCount2 = pstmt2.executeUpdate();

            System.out.println(pstmt2);
            System.out.println(rowCount2 + " rows matched");

            con.commit();

            return rowCount1 == 1 && rowCount2 == 1;
        } catch (SQLException e) {
            System.err.println("GroupRepository.changeOwner: Transaction failed; performing rollback.");
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println("GroupRepository.changeOwner: Rollback after failed transaction failed.");
                ex.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("GroupRepository.changeOwner: Failed to re-enable auto-commit after transaction.");
                ex.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean addMember(Group group, User member) {
        var sql = "INSERT INTO groupMembership (groupId, userNickname) VALUES (?, ?)";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, group.getId());
            pstmt.setString(2, member.getNickname());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeMember(Group group, User member) {
        var sql = "DELETE FROM groupMembership WHERE groupId = ? AND userNickname = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, group.getId());
            pstmt.setString(2, member.getNickname());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
