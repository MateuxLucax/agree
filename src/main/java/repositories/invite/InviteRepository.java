package repositories.invite;

import models.User;
import models.group.Group;
import models.invite.FriendInvite;
import models.invite.GroupInvite;
import models.invite.Invite;
import repositories.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InviteRepository implements IInviteRepository
{
    private final Connection con;

    public InviteRepository() {
        con = DBConnection.get();
    }

    @Override
    public List<FriendInvite> getFriendInvites(User user) {
        var invs = new ArrayList<FriendInvite>();
        var sql = "SELECT nicknameFrom, nicknameTo " +
                  "FROM FriendInvites " +
                  "WHERE nicknameFrom = ? OR nicknameTo = ?";
        try (var pstmt = con.prepareStatement(sql)) {
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
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getNickname());
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                var from      = new User(res.getString(1));
                var to        = new User(res.getString(2));
                var groupId   = res.getInt(3);
                var groupName = res.getString(4);
                var owner     = new User(res.getString(5));

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
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, inv.from().getNickname());
            pstmt.setString(2, inv.to().getNickname());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean addGroupInvite(GroupInvite inv) {
        var sql = "INSERT INTO GroupInvites (nicknameFrom, nicknameTo, groupId) VALUES (?, ?, ?)";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, inv.from().getNickname());
            pstmt.setString(2, inv.to().getNickname());
            pstmt.setInt   (3, inv.getGroup().getId());
            return pstmt.executeUpdate() == 1;
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

    private boolean removeFriendInvite(FriendInvite invite) {
        var sql = "DELETE FROM FriendInvites WHERE nicknameFrom = ? AND nicknameTo = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, invite.from().getNickname());
            pstmt.setString(2, invite.to().getNickname());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean removeGroupInvite(GroupInvite invite) {
        var sql = "DELETE FROM GroupInvites WHERE nicknameFrom = ? AND nicknameTo = ? AND groupId = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, invite.from().getNickname());
            pstmt.setString(2, invite.to().getNickname());
            pstmt.setInt   (3, invite.getGroup().getId());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean declineInvite(Invite invite) {
        return invite instanceof GroupInvite ?
                removeGroupInvite((GroupInvite) invite) :
                removeFriendInvite((FriendInvite) invite);
    }

    private boolean acceptGroupInvite(GroupInvite invite) {
        // This is another transaction. The two actions of accepting an invite
        // (removing it from the database) and adding a member to the group
        // are related, and if they're not both performed successfully,
        // we can't leave just one of them done (e.g. user accepts invite
        // but while removing the invite works, adding him to the group doesn't,
        // so he's gonna be like "But I clicked accept! Why am I not in the group?!" etc.)

        var sql1 = "DELETE FROM GroupInvites WHERE nicknameFrom=? AND nicknameTo=? AND groupId=?";
        var sql2 = "INSERT INTO GroupMembership (userNickname, groupId) VALUES (?, ?)";

        try (var pstmt1 = con.prepareStatement(sql1);
             var pstmt2 = con.prepareStatement(sql2))
        {
            con.setAutoCommit(false);

            String nickFrom = invite.from().getNickname();
            String nickTo   = invite.to().getNickname();
            int   groupId  = invite.getGroup().getId();

            pstmt1.setString(1, nickFrom);
            pstmt1.setString(2, nickTo);
            pstmt1.setInt   (3, groupId);
            pstmt1.executeUpdate();

            pstmt2.setString(1, nickTo);
            pstmt2.setInt   (2, groupId);
            pstmt2.executeUpdate();

            con.commit();

            return true;
        } catch (SQLException e) {
            System.err.println("InviteRepository.acceptGroupInvite:");
            System.err.println("\tTransaction failed; performing rollback.");
            try {

                con.rollback();

            } catch (SQLException ex) {
                System.err.println("InviteRepository.acceptGroupInvite:");
                System.err.println("\tRollback failed.");
                e.printStackTrace();
            }
        } finally {
            try {

                con.setAutoCommit(true);

            } catch (SQLException e) {
                System.err.println("InviteRepository.acceptGroupInvite:");
                System.err.println("\tFailed to re-enable auto-commit after transaction.");
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean acceptFriendInvite(Invite invite) {
        // Transaction: removes the friend invite and adds the friendship relaton.

        var sql1 = "DELETE FROM FriendInvites WHERE nicknameFrom=? AND nicknameTo=?";
        var sql2 = "INSERT INTO Friendship (nickname1, nickname2) VALUES (?, ?)";

        String nickFrom = invite.from().getNickname();
        String nickTo   = invite.to().getNickname();

        // Friendship relations are uniquely identified by the users involved
        // (by the (nickname1, nickname2) pair). But for each pair of users A and B,
        // there two possible ways to store the relation: (A, B) and (B, A).
        // Which one is actually stored? The answer is that we store (A, B) if A < B
        // and (B, A) otherwise.
        // [We could store both (A, B) and (B, A), which is nice in some ways,
        // but then there will be two records storing the same information, it's redundant.
        // Also, other tables having to refer to the Friendship table could be a problem too,
        // since the (nickname1, nickname2) pair is the primary key, but I'm just guessing.]
        // So that's why we store the users in order:
        String nick1, nick2;
        if (nickFrom.compareTo(nickTo) < 0) {
            nick1 = nickFrom;
            nick2 = nickTo;
        } else {
            nick1 = nickTo;
            nick2 = nickFrom;
        }

        try (var pstmt1 = con.prepareStatement(sql1);
             var pstmt2 = con.prepareStatement(sql2))
        {
            con.setAutoCommit(false);

            pstmt1.setString(1, nickFrom);
            pstmt1.setString(2, nickTo);
            pstmt1.executeUpdate();

            pstmt2.setString(1, nick1);
            pstmt2.setString(2, nick2);
            pstmt2.executeUpdate();

            con.commit();

            return true;
        } catch(SQLException e) {
            System.err.println("InviteRepository.acceptFriendInvite:");
            System.err.println("\tTransaction failed; performing rollback.");
            try {

                con.rollback();

            } catch(SQLException ex) {
                System.err.println("InviteRepository.acceptFriendInvite:");
                System.err.println("\tRollback failed.");
                ex.printStackTrace();
            }

            return false;
        } finally {
            try {

                con.setAutoCommit(true);

            } catch(SQLException e) {
                System.err.println("InviteRepository.acceptFriendInvite:");
                System.err.println("\tFailed to re-enable auto-commit after transaction.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean acceptInvite(Invite invite) {
        return invite instanceof GroupInvite
             ? acceptGroupInvite((GroupInvite) invite)
             : acceptFriendInvite(invite);
    }

}
