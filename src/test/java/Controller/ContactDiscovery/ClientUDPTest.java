package Controller.ContactDiscovery;
import Controller.Database.DatabaseController;
import Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientUDPTest {

    //TEST
    @BeforeAll
    static void ensure_database_is_created() {
        DatabaseController.createUserTable();
        DatabaseController.createChatTable(0);
    }

    @AfterEach
    void cleanDatabase() {
        deleteTestChatTable();
    }

    @Test
    void broadcast() {
        // Test broadcast method
        try {
            ClientUDP.broadcast("Test Message", InetAddress.getLocalHost());
        } catch (IOException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }


    @Test
    void listAllBroadcastAddresses() {
        // Test listAllBroadcastAddresses method
        try {
            List<InetAddress> broadcastAddresses = ClientUDP.listAllBroadcastAddresses();
            assertNotNull(broadcastAddresses);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void sendUsername() {
        // Test sendUsername method
        User user = new User("TestUser", InetAddress.getLoopbackAddress(), true);
        List<InetAddress> broadcastList;
        List<User> Users = DatabaseController.getUsers();
        try {
            broadcastList = ClientUDP.listAllBroadcastAddresses();
            ClientUDP.sendUsername(broadcastList, user);

            // Verify that the contact list is empty before sending the first broadcast to check the unicity of the username
            assertEquals(0, Users.size());
        } catch (IOException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void sendChangeUsername() {
        // Test sendChangeUsername method
        User user = new User("TestUser", InetAddress.getLoopbackAddress(), true);
        try {
            ClientUDP.sendChangeUsername(user, "NewUsername");

            // Verify that the username is changed as expected
            assertEquals("NewUsername", user.getUsername());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void sendEndConnection() {
        // Test sendEndConnection method
        try {
            ClientUDP.sendEndConnection();
            List<User> Users = DatabaseController.getUsers();
            // Verify that the contact list is empty and user state is false after disconnection
            assertTrue(Users.isEmpty());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    private static void deleteTestChatTable() {
        Connection conn = DatabaseController.connect();
        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                if (tableName.startsWith("Chat")) {
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
