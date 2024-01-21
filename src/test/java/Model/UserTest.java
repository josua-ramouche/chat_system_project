package Model;

import Controller.Database.DatabaseController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @BeforeAll
    static void setup() {
        DatabaseController.initConnection();
    }

    @AfterEach
    void afterEach() {
        cleanDatabase();
    }

    @Test
    void setAndGetUsernameTest() {
        User user = new User();
        // Test setter
        user.setUsername("TestUser");
        // Test getter
        assertEquals("TestUser", user.getUsername());

    }

    @Test
    void setAndGetIPAddressTest() throws UnknownHostException {
        User user = new User();
        // Test setter
        user.setIPAddress(InetAddress.getByName("192.168.0.1"));
        // Test getter
        assertEquals(InetAddress.getByName("192.168.0.1"), user.getIPAddress());
    }

    @Test
    void setAndGetStateTest() {
        User user = new User();
        // Test setter
        user.setState(true);
        // Test getter
        assertTrue(user.getState());
    }

    @Test
    void defaultConstructorTest() {
        User user = new User();
        assertNull(user.getUsername());
        assertNull(user.getIPAddress());
        assertNull(user.getState());
    }

    @Test
    void parameterizedConstructorTest() throws UnknownHostException {
        User user = new User("TestUser", InetAddress.getByName("192.168.0.1"), true);

        //Check that all parameters were initialized
        assertEquals("TestUser", user.getUsername());
        assertEquals(InetAddress.getByName("192.168.0.1"), user.getIPAddress());
        assertTrue(user.getState());
    }

    @Test
    void containsContactTest() throws UnknownHostException {
        User user1 = new User("User1", InetAddress.getByName("192.168.0.1"), true);
        User user3 = new User("User1", InetAddress.getByName("192.168.0.1"), true);

        //Add newly created users to a contactList
        List<User> contactList = new ArrayList<>();
        contactList.add(user1);

        //Check that the contact list is not empty with containsContact method
        assertTrue(user1.containsContact(contactList, user3));
    }


    @Test
    void hashCodeTest() throws UnknownHostException {
        User user1 = new User("User1", InetAddress.getByName("192.168.0.1"), true);
        User user2 = new User("User1", InetAddress.getByName("192.168.0.1"), true);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    //Delete all test chat tables from the database
    private void cleanDatabase() {
        Connection conn = DatabaseController.connect();
        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                if (tableName.startsWith("Chat")) {
                        // Delete only tables created during the test
                        stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName + ";");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}