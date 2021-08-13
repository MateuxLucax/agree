package repositories.group;

import models.User;
import models.group.Group;
import repositories.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository implements IGroupRepository
{
    private final Connection con;

    public GroupRepository()
    {
        con = DBConnection.get();
    }

    @Override
    public boolean createGroup(Group group) {
        var sql = "INSERT INTO groups (ownerNickname, name) VALUES (?, ?)";
        try (var pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, group.getOwner().getNickname());
            pstmt.setString(2, group.getName());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            boolean hasKeys = generatedKeys.next();
            if (hasKeys) {
                group.setId(generatedKeys.getInt(1));
            }
            // No generated keys means the insert didn't work
            return hasKeys;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteGroup(int id) {
        var sql = "DELETE FROM groups g WHERE g.id = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean renameGroup(Group group) {
        var sql = "UPDATE groups SET name = ? WHERE id = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, group.getName());
            pstmt.setInt(2, group.getId());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

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
                var id            = res.getInt(1);
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
    public List<User> getMembers(Group group) {
        var users = new ArrayList<User>();
        var sql = "SELECT m.userNickname FROM groupMembership m WHERE m.groupId = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, group.getId());
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

            int    groupId = group.getId();
            String newNick = newOwner.getNickname();
            String oldNick = group.getOwner().getNickname();

            pstmt1.setString(1, newNick);
            pstmt1.setInt   (2, groupId);
            pstmt1.executeUpdate();

            pstmt2.setString(1, oldNick);
            pstmt2.setString(2, newNick);
            pstmt2.setInt   (3, groupId);
            pstmt2.executeUpdate();

            con.commit();

            return true;
        } catch (SQLException e) {
            System.err.println("GroupRepository.changeOwner: Transaction failed; performing rollback.");
            try {

                con.rollback();

            } catch (SQLException ex) {
                System.err.println("GroupRepository.changeOwner: Rollback failed.");
                ex.printStackTrace();
            }

            return false;
        } finally {
            try {

                con.setAutoCommit(true);

            } catch (SQLException ex) {
                System.err.println("GroupRepository.changeOwner: Failed to re-enable auto-commit after transaction.");
                ex.printStackTrace();
            }
        }
    }


    @Override
    public boolean removeMember(Group group, User member) {
        var sql = "DELETE FROM groupMembership WHERE groupId = ? AND userNickname = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt   (1, group.getId());
            pstmt.setString(2, member.getNickname());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
