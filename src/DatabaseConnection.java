import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database connection utility for the Drama Club Membership Application.
 * Provides centralized database connection management.
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final String DB_URL = "jdbc:mysql://localhost:3306/club_membership";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Gets a connection to the MySQL database.
     * @return A connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found. Make sure you have mysql-connector-j added to your project", e);
            throw new SQLException("MySQL JDBC Driver not found. Please add mysql-connector-j to your project's libraries.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}