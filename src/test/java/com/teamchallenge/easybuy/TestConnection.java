package com.teamchallenge.easybuy;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        // Убрал serverTimezone
        String url = "jdbc:postgresql://localhost:5432/easybuy";
        String username = "postgres";
        String password = "postgres";

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");
            conn.close();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}