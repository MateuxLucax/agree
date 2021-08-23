package repositories.user;

import models.User;
import repositories.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository
{
    private final Connection con;
    private String defaultPicture;

    public UserRepository()
    {
        con = DBConnection.get();
        loadDefaultPicture();
    }

    private void loadDefaultPicture() {
        var sql = "SELECT column_default " +
                  "FROM information_schema.columns " +
                  "WHERE (table_schema, table_name, column_name) = ('public', 'users', 'profileimage')";
        try (var pstmt = con.prepareStatement(sql)) {
            var res = pstmt.executeQuery();
            if (res.next()) {
                // What you get is not "url", but "'url'::character varying"
                var columnDefault = res.getString(1);
                defaultPicture = columnDefault.substring(1, columnDefault.indexOf("'", 1));
            } else {
                System.err.println("UserRepository::loadDefaultPicture: Could not initialize the default picture");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        var sql = "UPDATE Users SET profileImage = ? WHERE nickname = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getPicture());
            pstmt.setString(2, user.getNickname());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createUser(User user)
    {
        String sql = "INSERT INTO users (nickname, password) VALUES (?, ?)";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getPassword());

            user.setPicture(defaultPicture);

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
                var user = new User(nickname);
                user.setPicture(picture);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
