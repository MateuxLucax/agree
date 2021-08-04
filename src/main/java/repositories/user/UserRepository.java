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
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
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
        if (userExists(user.getNickname()))
            return false;
        String sql = "INSERT INTO users (nickname, pass, creationDate) VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getPassword());
            pstmt.setTimestamp(3, Timestamp.from(user.getCreationDate().toInstant()));
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User getUser(String username, String password)
    {
        var sql = "SELECT u.nickname, u.pass, u.creationDate FROM users u WHERE u.nickname = ? AND u.pass = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                var creationDate = Date.from(res.getTimestamp(3).toInstant());
                return new User(username, password, creationDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> searchUsers(String search) {
        List<User> users = new ArrayList<>();
        var sql = "SELECT u.nickname, u.creationDate FROM users u WHERE u.nickname LIKE ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, '%'+search+'%');
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                var nickname = res.getString(1);
                var creationDate = Date.from(res.getTimestamp(2).toInstant());
                users.add(new User(nickname, creationDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
