import java.sql.*;

/**
 * Database connection utility for the Java Ambassadors Programming Club Membership Application.
 * Provides centralized database connection management.
 * This code take care of the XAMPP Connection.
 */
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/java_ambassadors_club";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Gets a connection to the MySQL database in XAMPP.
     * @return A connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure you have mysql-connector-j added to your project");
            throw new SQLException("MySQL JDBC Driver not found. Please add mysql-connector-j to your project's libraries.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}