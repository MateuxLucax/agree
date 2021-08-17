package repositories.message;

import models.User;
import models.group.Group;
import models.message.Message;
import repositories.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class MessageRepository implements IMessageRepository {

    private final Connection con;

    public MessageRepository() {
        con = DBConnection.get();
    }

    /**
     * Expects the ResultSet of a "SELECT id, message, sentAt, sentBy FROM GroupMessages/FriendMessages"
     */
    private List<Message> toMessageList(ResultSet results) throws SQLException {
        var messages = new ArrayList<Message>();
        while (results.next()) {
            int    id     = results.getInt(1);
            String text   = results.getString(2);
            Date   sentAt = Date.from(results.getTimestamp(3).toInstant());
            var sentBy = new User(results.getString(4));
            sentBy.setPicture(results.getString(5));

            messages.add(new Message(id, sentBy, text, sentAt));
        }
        return messages;
    }

    public boolean addMessage(Group group, Message message) {
        var sql = "INSERT INTO GroupMessages (groupId, message, sentAt, sentBy) VALUES (?, ?, ?, ?)";
        try (var pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt      (1, group.getId());
            pstmt.setString   (2, message.getText());
            pstmt.setTimestamp(3, Timestamp.from(message.sentAt().toInstant()));
            pstmt.setString   (4, message.getUser().getNickname());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            boolean hasKeys = generatedKeys.next();
            if (hasKeys) {
                message.setId(generatedKeys.getInt(1));
            }
            return hasKeys;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeGroupMessage(Message msg) {
        var sql = "DELETE FROM GroupMessages WHERE id = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, msg.getId());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> getNewestMessages(Group group, int numberOfMessages) {
        // LIMIT will get the top N rows instead of the bottom N rows.
        // This means that we need to do query for messages in *decreasing* order
        // to get the N newest ones. But since we want them in ascending order
        // in the array, we'll need to wrap that query in another query that'll sort
        // in the order we want.
        var sql = "SELECT m.id, m.message, m.sentAt, m.sentBy, m.profileimage " +
                  "FROM (SELECT gm.id, gm.message, gm.sentAt, gm.sentBy, u.profileimage "+
                  "      FROM GroupMessages gm" +
                  "      JOIN users u on u.nickname = gm.sentby " +
                  "      WHERE gm.groupId = ? "+
                  "      ORDER BY gm.sentAt DESC "+
                  "      LIMIT ?) AS m " +
                  "ORDER BY m.sentAt";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, group.getId());
            pstmt.setInt(2, numberOfMessages);
            return toMessageList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getMessagesAfter(Group group, Date date) {
        var sql = "SELECT gm.id, gm.message, gm.sentAt, gm.sentBy, u.profileimage " +
                  "FROM GroupMessages gm " +
                  "JOIN users u on u.nickname = gm.sentBy " +
                  "WHERE gm.groupId = ? " +
                  "AND   gm.sentAt  > ?" +
                  "ORDER BY gm.sentAt";

        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt      (1, group.getId());
            pstmt.setTimestamp(2, Timestamp.from(date.toInstant()));
            return toMessageList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getMessagesBefore(Group group, Date date, int numberOfMessages) {
        // See getNewestGroupMessages for why the query is like this.
        var sql = "SELECT m.id, m.message, m.sentAt, m.sentBy, m.profileimage " +
                  "FROM (SELECT gm.id, gm.message, gm.sentAt, gm.sentBy, u.profileimage " +
                  "      FROM GroupMessages gm " +
                  "      JOIN users u on u.nickname = gm.sentby " +
                  "      WHERE gm.groupId = ? " +
                  "      AND   gm.sentAt  < ?" +
                  "      ORDER BY gm.sentAt DESC " +
                  "      LIMIT ?) AS m " +
                  "ORDER BY m.sentAt";

        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt      (1, group.getId());
            pstmt.setTimestamp(2, Timestamp.from(date.toInstant()));
            pstmt.setInt      (3, numberOfMessages);
            return toMessageList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addMessages(User friend1, User friend2, Message message) {
        String nick1 = friend1.getNickname();
        String nick2 = friend2.getNickname();
        if (nick1.compareTo(nick2) > 0) { String t = nick1; nick1 = nick2; nick2 = t; }

        var sql = "INSERT INTO FriendMessages (nickname1, nickname2, message, sentAt, sentBy) VALUES (?, ?, ?, ?, ?)";
        try (var pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString   (1, nick1);
            pstmt.setString   (2, nick2);
            pstmt.setString   (3, message.getText());
            pstmt.setTimestamp(4, Timestamp.from(message.sentAt().toInstant()));
            pstmt.setString   (5, message.getUser().getNickname());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            boolean hasKeys = generatedKeys.next();
            if (hasKeys) {
                message.setId(generatedKeys.getInt(1));
            }
            return hasKeys;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean removeFriendMessage(Message msg) {
        var sql = "DELETE FROM FriendMessages WHERE id = ?";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, msg.getId());
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> getNewestMessages(User friend1, User friend2, int numberOfMessages) {
        String nick1 = friend1.getNickname();
        String nick2 = friend2.getNickname();
        if (nick1.compareTo(nick2) > 0) { String t = nick1; nick1 = nick2; nick2 = t; }

        var sql = "SELECT m.id, m.message, m.sentAt, m.sentBy " +
                  "FROM (SELECT id, message, sentAt, sentBy " +
                  "      FROM FriendMessages" +
                  "      WHERE nickname1 = ? AND nickname2 = ?" +
                  "      ORDER BY sentAt DESC" +
                  "      LIMIT ?) AS m " +
                  "ORDER BY m.sentAt";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, nick1);
            pstmt.setString(2, nick2);
            pstmt.setInt   (3, numberOfMessages);
            return toMessageList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getMessagesAfter(User friend1, User friend2, Date date) {
        String nick1 = friend1.getNickname();
        String nick2 = friend2.getNickname();
        if (nick1.compareTo(nick2) > 0) { String t = nick1; nick1 = nick2; nick2 = t; }

        var sql = "SELECT id, message, sentAt, sentBy " +
                  "FROM FriendMessages " +
                  "WHERE nickname1 = ? AND nickname2 = ?" +
                  "AND sentAt > ?" +
                  "ORDER BY sentAt";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString   (1, nick1);
            pstmt.setString   (2, nick2);
            pstmt.setTimestamp(3, Timestamp.from(date.toInstant()));
            return toMessageList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getMessagesBefore(User friend1, User friend2, Date date, int numberOfMessages) {
        String nick1 = friend1.getNickname();
        String nick2 = friend2.getNickname();
        if (nick1.compareTo(nick2) > 0) { String t = nick1; nick1 = nick2; nick2 = t; }

        var sql = "SELECT m.id, m.message, m.sentAt, m.sentBy " +
                  "FROM (SELECT id, message, sentAt, sentBy " +
                  "      FROM FriendMessages" +
                  "      WHERE nickname1 = ? AND nickname2 = ? " +
                  "      AND sentAt < ? " +
                  "      ORDER BY sentAt DESC " +
                  "      LIMIT ?) AS m " +
                  "ORDER BY sentAt";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setString   (1, nick1);
            pstmt.setString   (2, nick2);
            pstmt.setTimestamp(3, Timestamp.from(date.toInstant()));
            pstmt.setInt      (4, numberOfMessages);
            return toMessageList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
