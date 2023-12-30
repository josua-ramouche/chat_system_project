package Controller.Database;


import Model.User;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.concurrent.TimeUnit;

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
                    + "     connectionState BOOLEAN NOT NULL, \n "
                    + "     UNIQUE (ipaddress) \n"
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
                    + "		contactID INTEGER PRIMARY KEY, \n"
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
                    + "     timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, \n"
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

    public static void addUser(User u){
        String sql = "INSERT OR IGNORE INTO Users (username,ipaddress,connectionState) VALUES (?,?,?);";
        Connection conn = connect();
        //SQL Statement to add a user to table Users
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,u.getUsername());
            pstmt.setString(2,u.getIPAddress().getHostAddress());
            pstmt.setBoolean(3,u.getState());
            pstmt.executeUpdate();
            int id = getUserID(u);
            if (id != 0){
                addContact(id, conn);
            }
            System.out.println("User added successfully to database\n");
        }
        catch (SQLException e) {
            System.out.println("User addition to database failed\n");
            e.printStackTrace();
        }
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

    public static int getUserID(User u){
        Connection conn = connect();
        int id = 0;
        String sql = "SELECT userID FROM Users WHERE username = ? AND ipaddress = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getIPAddress().getHostAddress());

            ResultSet resultSet = pstmt.executeQuery();

            while(resultSet.next()) {
                id = resultSet.getInt("userID");
            }
            return id;
        }
        catch (SQLException e) {
            System.out.println("Could not get userID in database\n");
            e.printStackTrace();
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return id;
    }

    public static void disconnectUser(User u) {
        String sql = "UPDATE Users " +
                "SET connectionState = '0' " +
                "WHERE username = ? AND ipaddress = ?;";

        Connection conn = connect();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,u.getUsername());
            pstmt.setString(2,u.getIPAddress().getHostAddress());
            pstmt.executeUpdate();
            System.out.println("User disconnection transmitted to database\n");
        }
        catch (SQLException e) {
            System.out.println("User disconnection transmission to database failed\n");
        }
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

    public static void updateUsername(User u, String name) {
        String sql = "UPDATE OR IGNORE Users " +
                "SET username = ? " +
                "WHERE username = ? AND ipaddress = ?;";

        Connection conn = connect();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,name);
            pstmt.setString(2,u.getUsername());
            pstmt.setString(3,u.getIPAddress().getHostAddress());
            pstmt.executeUpdate();
            System.out.println("Username updated in database\n");
        }
        catch (SQLException e) {
            System.out.println("Username update failed in database\n");
            e.printStackTrace();
        }
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

    private static void addContact(int id, Connection conn){
        String sql = "INSERT OR IGNORE INTO Contacts (contactID) VALUES (?);"; //IGNORE might be an obstacle
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.print("User addition to Contacts table succeeded\n");
        }
        catch (SQLException e) {
            System.out.print("User addition to Contacts table failed\n");
            e.printStackTrace();
        }
    }

    public static void saveSentMessage(int id, String message) {
        String sql = "INSERT INTO Chat" + id + " (message,senderID) VALUES (?,?);";

        Connection conn = connect();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,message);
            pstmt.setInt(2, 0);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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

    public static void saveReceivedMessage(int id, String message) {
        String sql = "INSERT INTO Chat" + id + " (message,senderID) VALUES (?,?);";

        Connection conn = connect();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,message);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        /*User contact1 = new User();
        contact1.setUsername("Contact1");
        contact1.setIPAddress(InetAddress.getByName("198.162.5.1"));
        contact1.setState(true);

        User contact2 = new User();
        contact2.setUsername("Contact2");
        contact2.setIPAddress(InetAddress.getByName("198.162.5.2"));
        contact2.setState(true);

        User contact3 = new User();
        contact3.setUsername("Contact3");
        contact3.setIPAddress(InetAddress.getByName("198.162.5.3"));
        contact3.setState(false);

        addUser(contact1);
        addUser(contact2);
        addUser(contact3);

        TimeUnit.SECONDS.sleep(10);

        updateUsername(contact3,"newUsername");
        disconnectUser(contact1);*/

        saveReceivedMessage(1,"Hey!");
        saveSentMessage(1,"Helluw!");

    }
}

