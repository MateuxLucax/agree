package repositories.message;

import models.User;
import models.group.Group;
import models.message.Message;
import repositories.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;

public class MessageRepository {

    private final Connection con;

    public MessageRepository() {
        con = DBConnection.get();
    }

    public boolean addGroupMessage(Group group, Message message) {
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

    /**
     * Expects the ResultSet of a "SELECT id, message, sentAt, sentBy FROM <GroupMessages or FriendMessages>"
     */
    private List<Message> toList(ResultSet results) throws SQLException {
        var messages = new ArrayList<Message>();
        while (results.next()) {
            int    id     = results.getInt(1);
            String text   = results.getString(2);
            Date   sentAt = Date.from(results.getTimestamp(3).toInstant());
            User   sentBy = new User(results.getString(4));

            messages.add(new Message(id, sentBy, text, sentAt));
        }
        return messages;
    }

    /**
     * Returns in ascending order (from oldest to newest)
     */
    public List<Message> getNewestGroupMessages(Group group, int numberOfMessages) {
        // LIMIT will get the top N rows instead of the bottom N rows.
        // This means that we need to do query for messages in *decreasing* order
        // to get the N newest ones. But since we want them in ascending order
        // in the array, we'll need to wrap that query in another query that'll sort
        // in the order we want.
        var sql = "SELECT m.id, m.message, m.sentAt, m.sentBy " +
                  "FROM (SELECT id, message, sentAt, sentBy "+
                  "      FROM GroupMessages " +
                  "      WHERE groupId = ? "+
                  "      ORDER BY sentAt DESC "+
                  "      LIMIT ?) AS m " +
                  "ORDER BY m.sentAt ASC";
        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, group.getId());
            pstmt.setInt(2, numberOfMessages);
            return toList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getGroupMessagesAfter(Group group, Date date) {
        var sql = "SELECT id, message, sentAt, sentBy " +
                  "FROM GroupMessages " +
                  "WHERE groupId = ? " +
                  "AND   sentAt  > ?" +
                  "ORDER BY sentAt";

        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt      (1, group.getId());
            pstmt.setTimestamp(2, Timestamp.from(date.toInstant()));
            return toList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getGroupMessagesBefore(Group group, Date date, int numberOfMessages) {
        // See getNewestGroupMessages for why the query is like this.
        var sql = "SELECT m.id, m.message, m.sentAt, m.sentBy " +
                  "FROM (SELECT id, message, sentAt, sentBy " +
                  "      FROM GroupMessages " +
                  "      WHERE groupId = ? " +
                  "      AND   sentAt  < ?" +
                  "      ORDER BY sentAt DESC " +
                  "      LIMIT ?) AS m " +
                  "ORDER BY sentAt";

        try (var pstmt = con.prepareStatement(sql)) {
            pstmt.setInt      (1, group.getId());
            pstmt.setTimestamp(2, Timestamp.from(date.toInstant()));
            pstmt.setInt      (3, numberOfMessages);
            return toList(pstmt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
