package Controller.Database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseController {

    public static Connection connect() {
        Connection conn = null;
        try {
            //Database parameters
            String url = "jdbc:sqlite:chatsystem.db";
            //Create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite database has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    // Create a User Table in database
    public static void createUserTable() {
        Connection conn = connect();
        // Create User table after connection to the database
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                    + "		userID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                    + "		username TEXT NOT NULL, \n"
                    + "     ipaddress TEXT NOT NULL, \n"
                    + "     connectionState BOOLEAN NOT NULL \n"
                    + ");";
            stmt.executeUpdate(sql);
            System.out.println("User table created successfully\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // End the connection after the creation of the table
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //Create a Contact Table in database
    public static void createContactTable() {
        Connection conn = connect();
        // Create Contact table after connection to the database
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Contacts (\n"
                    + "		contactID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                    + "     FOREIGN KEY (contactID) REFERENCES Users(userId) \n"
                    + ");";
            stmt.executeUpdate(sql);
            System.out.println("Contacts table created successfully\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // End the connection after the creation of the table
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //Create a Chat Table in the database
    public static void createChatTable(int id) {
        Connection conn = connect();
        // Create Contact table after connection to the database
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Chat" + id + " (\n"
                    + "		messageID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                    + "		message TEXT NOT NULL, \n"
                    + "		senderID INTEGER, \n"
                    + "     time DATETIME NOT NULL, \n"
                    + "     FOREIGN KEY (senderID) REFERENCES Contacts(contactID) \n"
                    + ");";
            stmt.executeUpdate(sql);
            System.out.println("Chat table created successfully\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // End the connection after the creation of the table
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        createChatTable(1);
    }
}

