package KiemTra.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class MyDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/kiemtra_ss14";
    private static final String USER = "root";
    private static final String PASSWORD = "Londeocan1";


    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database");
            return conn;

        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return null;

    }
}
