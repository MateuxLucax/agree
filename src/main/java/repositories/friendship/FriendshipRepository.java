package repositories.friendship;

import models.User;
import repositories.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        var sql = "SELECT f.nickname2 FROM friendship f WHERE f.nickname1 = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getNickname());
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
    public boolean addFriend(User friend1, User friend2) {
        // Friendship is symmetric: if A is friends with B, B is friends with A
        var nick1 = friend1.getNickname();
        var nick2 = friend2.getNickname();
        var sql = "INSERT INTO friendship(nickname1, nickname2) VALUES (?, ?), (?, ?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, nick1);
            pstmt.setString(2, nick2);
            pstmt.setString(3, nick2);
            pstmt.setString(4, nick1);
            pstmt.execute();
            return pstmt.getUpdateCount() == 2;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeFriend(User friend1, User friend2) {
        var nick1 = friend1.getNickname();
        var nick2 = friend2.getNickname();
        var sql = "DELETE FROM friendship f WHERE " +
                  "(f.nickname1 = ? AND f.nickname2 = ?) OR " +
                  "(f.nickname1 = ? AND f.nickname2 = ?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, nick1);
            pstmt.setString(2, nick2);
            pstmt.setString(3, nick2);
            pstmt.setString(4, nick1);
            pstmt.execute();
            return pstmt.getUpdateCount() == 2;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}