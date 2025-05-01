package com.example.escaperoombusinesssystem;

public class Admin extends User {

    public Admin(String username , String plainTextPassword) {
        super(username , "Admin" , plainTextPassword);
    }

    // Verifying a Password (e.g., During Login)
    // boolean isPasswordCorrect = admin.verifyPassword("wrongPass"); // Returns false
    //  boolean isPasswordCorrect = admin.verifyPassword("securePass123"); // Returns true

   //public void accessDashboard(){}
   //public com.example.escaperoombusinesssystem.Report generateReport(){}
   // public void addRoom(){}


}
