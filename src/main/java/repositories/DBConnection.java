package repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection
{
    private static final String DB_URL = "jdbc:postgresql:agree";
    private static final String USER   = "postgres";
    private static final String PASS   = "123";

    private static Connection connection;

    public static Connection get()
    {
        if (connection == null) {
            System.out.println("Connecting to DB...");
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void disconnect()
    {
        if (connection != null) {
            System.out.println("Disconnecting from DB...");
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connection = null;
    }
}
