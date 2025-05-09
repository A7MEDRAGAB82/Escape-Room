package com.example.escaperoombusinesssystem.model;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {

    /**
     * Establishes a connection to Supabase PostgreSQL database
     *
     * @return Connection object if successful, null if connection fails
     */
    public static Connection connect() {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Supabase-specific connection parameters
        String url = dotenv.get("SUPABASE_DB_URL"); // Format: "jdbc:postgresql://db.[project-ref].supabase.co:5432/postgres"
        String user = dotenv.get("SUPABASE_DB_USER"); // Typically "postgres"
        String password = dotenv.get("SUPABASE_DB_PASSWORD");

        // Initialize connection variable
        Connection con = null;

        try {
            // Load PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Attempt to establish a connection
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Supabase successfully");
        } catch(Exception e) {
            System.err.println("Error connecting to Supabase: " + e.getMessage());
            e.printStackTrace();
        }

        return con;
    }
}