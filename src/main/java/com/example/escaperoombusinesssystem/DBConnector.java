package com.example.escaperoombusinesssystem;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
    public static Connection connect(){
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        Connection con = null;

        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }


        return con;
    }
}
