package repositories.user;

import models.User;
import repositories.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository
{
    private final Connection con;

    public UserRepository()
    {
        con = DBConnection.get();
    }

    @Override
    public boolean userExists(String username)
    {
        var sql = "SELECT u.nickname FROM users u WHERE u.nickname = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet res = pstmt.executeQuery();
            return res.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean storeUser(User user)
    {
        // TODO is this necessary? the db will already disallow that
        if (userExists(user.getNickname()))
            return false;

        String sql = "INSERT INTO users (nickname, password) VALUES (?, ?)";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getPassword());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User getUser(String username, String password)
    {
        var sql = "SELECT u.profileimage FROM users u WHERE u.nickname = ? AND u.password = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                var picture = res.getString("profileImage");
                return new User(username, password, picture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> searchUsers(String search) {
        List<User> users = new ArrayList<>();
        var sql = "SELECT u.nickname, u.profileimage FROM users u WHERE u.nickname LIKE ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, '%'+search+'%');
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                var nickname = res.getString("nickname");
                var picture  = res.getString("profileImage");
                users.add(new User(nickname, picture));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
