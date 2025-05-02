package com.example.escaperoombusinesssystem;

import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public abstract class User {
    protected String username, role;
    private String hashedPassword; // Store only the hashed version!

    // Updated constructor to include password hashing
    public User(String username, String role, String plainTextPassword) {
        this.username = username;
        this.role = role;
        this.hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt()); // Auto-salting
    }

    // Verify a password against the stored hash
    public boolean verifyPassword(String plainTextPassword) {
        return BCrypt.checkpw(plainTextPassword, this.hashedPassword);
    }


    public String getHashedPassword() {
        return hashedPassword; // Warning: Only expose for storage, never log/print!
    }


    // Existing methods
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public abstract void accessDashboard(Stage stage);
}