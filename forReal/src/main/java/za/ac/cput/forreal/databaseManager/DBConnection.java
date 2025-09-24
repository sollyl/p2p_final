package za.ac.cput.forreal.databaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
   
        public static Connection connect() {
        Connection con = null;
        try {
            String URL = "jdbc:mysql://localhost:3306/p2p_app?serverTimezone=Africa/Johannesburg";
            String Username = "teamuser";
            String Password = "1234";
            
            con = DriverManager.getConnection(URL, Username, Password);
            System.out.println("Connected successfully");
        }
        catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());   
        }
        return con;
    }
    
}
