package repositories.friendship;

import models.User;
import repositories.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendshipRepository implements IFriendshipRepository
{
    private Connection con;

    public FriendshipRepository()
    {
        con = DBConnection.get();
    }

    @Override
    public List<User> getFriends(User user) {
        var friends = new ArrayList<User>();

        var sql = "SELECT f.nickname2 FROM friendship f WHERE f.nickname1 = ? " +
                  "UNION " +
                  "SELECT f.nickname1 FROM friendship f WHERE f.nickname2 = ?";

        try (var pstmt = con.prepareStatement(sql)) {
            String nickname = user.getNickname();
            pstmt.setString(1, nickname);
            pstmt.setString(2, nickname);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                friends.add(new User(res.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public boolean removeFriend(User friend1, User friend2) {
        var nick1 = friend1.getNickname();
        var nick2 = friend2.getNickname();

        // Why the order? see InviteRepository.acceptFriendInvite()
        if (nick1.compareTo(nick2) > 0) {   // if nick1 > nick2
            String temp = nick1;            // swap them so nick1 < nick2
            nick1 = nick2;
            nick2 = temp;
        }

        var sql = "DELETE FROM friendship f WHERE (f.nickname1 = ? AND f.nickname2 = ?)";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, nick1);
            pstmt.setString(2, nick2);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
