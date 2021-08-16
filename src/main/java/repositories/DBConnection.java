package repositories;

import app.config.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection
{
    private static Database database;
    private static Connection connection;

    public static void setUp(Database database) {
        DBConnection.database = database;
    }

    public static Connection get() {
        if (connection == null) {
            System.out.println("Connecting to DB...");
            try {
                var props = new Properties();
                props.setProperty("user", database.getUser());
                props.setProperty("password", database.getPassword());
                props.setProperty("connectTimeout", "300");
                props.setProperty("socketTimeout", "300");
                props.setProperty("testOnBorrow", "true");
                props.setProperty("validationQuery", "select 1");

                connection = DriverManager.getConnection(database.getUrl(), props);
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
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
