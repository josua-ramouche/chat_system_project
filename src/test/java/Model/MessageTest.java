package Model;
import Controller.Database.DatabaseController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @BeforeAll
    static void setup() {
        DatabaseController.initConnection();
    }

    @AfterEach
    void afterEach() {
        cleanDatabase();
    }

    @Test
    void messageConstructorTest() {
        User sender = new User("senderUsername", InetAddress.getLoopbackAddress(), true);
        Message message = new Message("Hello World!", "04/01/2024", sender);

        assertEquals("Hello World!", message.getContent());
        assertEquals("04/01/2024", message.getDate());
        assertEquals(sender, message.getSender());
    }

    @Test
    void messageGettersTest() {
        User sender = new User("senderUsername", InetAddress.getLoopbackAddress(), true);
        Message message = new Message("Hello, World!", "2024-01-04", sender);

        assertEquals("Hello, World!", message.getContent());
        assertEquals("2024-01-04", message.getDate());
        assertEquals(sender, message.getSender());
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
