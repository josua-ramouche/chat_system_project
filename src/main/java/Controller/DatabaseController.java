package Controller;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController {

    public static void connect() {
        Connection conn = null;
        try {
            //Database parameters
            String url = "jdbc:sqlite:sample.db";
            //Create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite database has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if(conn != null) {
                    conn.close();
                }
            }  catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        connect();
    }
}

