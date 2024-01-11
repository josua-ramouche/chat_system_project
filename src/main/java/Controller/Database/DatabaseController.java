package Controller.Database;


import Model.Message;
import Model.User;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    public static Connection connect() {
        Connection conn = null;
        try {
            //Database parameters
            String url = "jdbc:sqlite:chatsystem.db";
            //Create a connection to the database
            conn = DriverManager.getConnection(url);

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
                    + "     FOREIGN KEY (senderID) REFERENCES Users(userID) \n"
                    + ");";
            stmt.executeUpdate(sql);
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

    public static void initConnection() {
        createUserTable();
        String sql = "UPDATE Users " +
                "SET connectionState = '0'; ";
        Connection conn = connect();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.print("Connection states reset in database");
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

    public static void addUser(User u){
        if(!u.getUsername().equals("")) {
            String sql = "INSERT OR IGNORE INTO Users (username,ipaddress,connectionState) VALUES (?,?,?);";
            Connection conn = connect();
            //SQL Statement to add a user to table Users
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, u.getUsername());
                pstmt.setString(2, u.getIPAddress().getHostAddress());
                pstmt.setBoolean(3, u.getState());
                pstmt.executeUpdate();
                System.out.println("User added successfully to database\n");
            } catch (SQLException e) {
                System.out.println("User addition to database failed\n");
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static int getUserID(User u){
        Connection conn = connect();
        int id = 0;
        String sql = "SELECT userID FROM Users WHERE ipaddress = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getIPAddress().getHostAddress());

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

    public static int getUserID2(InetAddress u){
        Connection conn = connect();
        int id = 0;
        String sql = "SELECT userID FROM Users WHERE ipaddress = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getHostAddress());

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

    public static void updateConnectionState(User u,boolean state) {
        String sql = "UPDATE Users " +
                "SET connectionState = ? " +
                "WHERE username = ? AND ipaddress = ?;";

        Connection conn = connect();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1,state);
            pstmt.setString(2,u.getUsername());
            pstmt.setString(3,u.getIPAddress().getHostAddress());
            pstmt.executeUpdate();
            System.out.println("User's state of connection transmitted to database\n");
        }
        catch (SQLException e) {
            System.out.println("User's state of connection transmission to database failed\n");
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
        if(!u.getUsername().equals("")) {
            String sql = "UPDATE OR IGNORE Users " +
                    "SET username = ? " +
                    "WHERE userID = ? AND ipaddress = ?;";

            Connection conn = connect();

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, getUserID(u));
                pstmt.setString(3, u.getIPAddress().getHostAddress());
                pstmt.executeUpdate();
                System.out.println("Username updated in database\n");
            } catch (SQLException e) {
                System.out.println("Username update failed in database\n");
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static boolean containsUser(User u) {
        int id = getUserID(u);
        return id != 0;
    }

    public static List<User> getUsers(){
        List<User> users = new ArrayList<>();
        String username;
        InetAddress ipaddress;
        boolean state;
        Connection conn = connect();
        String sql = "SELECT * FROM Users WHERE connectionState='1';";
        try (Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);

            while(resultSet.next()) {
                username = resultSet.getString("username");
                ipaddress = InetAddress.getByName(resultSet.getString("ipaddress"));
                state = resultSet.getBoolean("connectionState");

                User user = new User(username,ipaddress,state);
                users.add(user);
            }
            System.out.println("Connected users retrieved from database successfully");
            return users;
        }
        catch (SQLException | UnknownHostException e) {
            System.out.println("Could not get messages in database\n");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return users;
    }

    public static List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        String username;
        InetAddress ipaddress;
        boolean state;
        Connection conn = connect();
        String sql = "SELECT * FROM Users;";
        try (Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);

            while(resultSet.next()) {
                username = resultSet.getString("username");
                ipaddress = InetAddress.getByName(resultSet.getString("ipaddress"));
                state = resultSet.getBoolean("connectionState");

                User user = new User(username,ipaddress,state);
                users.add(user);
            }
            System.out.println("Users retrieved from database successfully");
            return users;
        }
        catch (SQLException | UnknownHostException e) {
            System.out.println("Could not get messages in database\n");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return users;
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

    public static User getUser(int id) {
        User user = new User();
        String sql = "SELECT * FROM Users WHERE userID = ?;";

        Connection conn = connect();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,id);

            ResultSet resultSet = pstmt.executeQuery();

            while(resultSet.next()) {
                user.setUsername(resultSet.getString("username"));
                user.setIPAddress(InetAddress.getByName(resultSet.getString("ipaddress")));
                user.setState(resultSet.getBoolean("connectionState"));
            }
            return user;
        }
        catch (SQLException | UnknownHostException e) {
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
        return user;
    }

    public static List<Message> getMessages(int id){
        List<Message> messages = new ArrayList<>();
        String content;
        int senderID;
        String timeStamp;
        Connection conn = connect();
        String sql = "SELECT * FROM Chat" + id +";";
        try (Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);

            while(resultSet.next()) {
                content = resultSet.getString("message");
                senderID = resultSet.getInt("senderID");
                timeStamp = resultSet.getString("timestamp");

                User user = getUser(senderID);

                Message message = new Message(content,timeStamp,user);
                messages.add(message);
            }
            System.out.println("Messages from Chat" + id + " retrieved successfully");
            return messages;
        }
        catch (SQLException e) {
            System.out.println("Could not get messages in database\n");
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
        return messages;
    }

    public static synchronized void printContactList(){
        System.out.println("Print Contact List :");
        List<User> Users = DatabaseController.getUsers();
        Users.forEach(u -> {
            if (u.getState()) {
                System.out.println("user : " + u.getUsername());
            }
        });
    }

    public static void deleteUser(User u) {
        String sql = "DELETE FROM Users WHERE userID=?;";

        Connection conn = connect();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,getUserID(u));
            pstmt.executeUpdate();
            System.out.println("User deleted from database successfully");
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
}