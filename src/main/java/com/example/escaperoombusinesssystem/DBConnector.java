package com.example.escaperoombusinesssystem;

// Import external libraries/classes needed for this file
import io.github.cdimascio.dotenv.Dotenv;  // For loading .env files
import java.sql.Connection;               // JDBC interface for database connections
import java.sql.DriverManager;           // JDBC class for managing database drivers

/**
 * A utility class to establish and return a database connection.
 * Uses environment variables for secure credential management.
 */
public class DBConnector {

    /**
     * Establishes a connection to the database using credentials from .env file.
     *
     * @return Connection object if successful, null if connection fails
     */
    public static Connection connect() {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Retrieve database credentials from environment variables
        String url = dotenv.get("DB_URL");       // Database connection URL (e.g., jdbc:mysql://...)
        String user = dotenv.get("DB_USER");     // Database username
        String password = dotenv.get("DB_PASSWORD"); // Database password

        // Initialize connection variable
        Connection con = null;

        try {
            // Attempt to establish a connection using DriverManager
            con = DriverManager.getConnection(url, user, password);
            // Success message (consider using a logger in production)
            System.out.println("Connected to the database successfully");
        }
        catch(Exception e) {
            // Print error message if connection fails (e.g., wrong credentials, DB offline)
            System.out.println(e.getMessage());
        }

        // Return the connection object (may be null if failed)
        return con;
    }
}