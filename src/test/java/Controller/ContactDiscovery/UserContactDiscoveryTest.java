package Controller.ContactDiscovery;

import Controller.Database.DatabaseController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserContactDiscoveryTest {

    @BeforeAll
    static void setup() {
        DatabaseController.initConnection();
    }

    @BeforeAll
    static void ensure_database_is_created() {
        DatabaseController.createUserTable();
    }

    @AfterEach
    void afterEach() {
        cleanDatabase();
    }

    @Test
    void initUserTest() {
        try {
            UserContactDiscovery.inituser("testUser");
            //Check that attribute "temp" was initialized
            assertEquals("testUser", UserContactDiscovery.temp.getUsername());
            assertNotNull(UserContactDiscovery.temp.getIPAddress());
            assertTrue(UserContactDiscovery.temp.getState());
        } catch (UnknownHostException | SocketException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void initTest() {
        try {
            ServerUDP.EchoServer echoServer = UserContactDiscovery.Init();
            //Check that echoServer was created
            assertNotNull(echoServer);
        } catch (SocketException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void actionTest() {
        //Check that method "Action" does not raise an exception while running
        try {
            UserContactDiscovery userContactDiscovery = new UserContactDiscovery("testUser");
            userContactDiscovery.Action();
        } catch (UnknownHostException | SocketException | InterruptedException e) {
            fail("Exception thrown: " + e.getMessage());
        }
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
