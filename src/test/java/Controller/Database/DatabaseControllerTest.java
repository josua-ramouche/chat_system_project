package Controller.Database;

import Model.Message;
import Model.User;
import org.junit.jupiter.api.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest {

    @BeforeAll
    static void setup() {
        DatabaseController.initConnection();
    }

    @BeforeEach
    void beforeEach() {
        cleanDatabase();
        DatabaseController.createUserTable();
        DatabaseController.createChatTable(1);
    }

    @AfterEach
    void afterEach() {
        cleanDatabase();
    }



    @Test
    void testCreateUserTable() {
        DatabaseController.createUserTable();
        assertTrue(isTableExists("Users"));
    }

    @Test
    void testCreateChatTable() {
        int chatId = 1;
        DatabaseController.createChatTable(chatId);
        assertTrue(isTableExists("Chat" + chatId));
    }

    @Test
    void testInitConnection() {
        DatabaseController.initConnection();
        List<User> users = DatabaseController.getUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void testAddUser() {
        User user = new User("testUser", getInetAddress(), true);
        DatabaseController.addUser(user);
        assertTrue(DatabaseController.containsUser(user));
    }

    @Test
    void testUpdateUsername() {
        User user = new User("testUser", getInetAddress(), true);
        DatabaseController.addUser(user);
        String newUsername = "updatedUser";
        DatabaseController.updateUsername(user, newUsername);
        User updatedUser = DatabaseController.getUser(DatabaseController.getUserID(user));
        assertEquals(newUsername, updatedUser.getUsername());
    }

    @Test
    void testUpdateConnectionState() {
        User user = new User("testUser", getInetAddress(), true);
        DatabaseController.addUser(user);
        boolean newState = false;
        DatabaseController.updateConnectionState(user, newState);
        User updatedUser = DatabaseController.getUser(DatabaseController.getUserID(user));
        assertEquals(newState, updatedUser.getState());
    }

    @Test
    void testContainsUser() {
        User user = new User("testUser", getInetAddress(), true);
        assertFalse(DatabaseController.containsUser(user));
        DatabaseController.addUser(user);
        assertTrue(DatabaseController.containsUser(user));
    }

    @Test
    void testGetUsers() {
        List<User> users = DatabaseController.getUsers();
        assertNotNull(users);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = DatabaseController.getAllUsers();
        assertNotNull(users);
    }

    @Test
    void testSaveSentMessage() {
        int chatId = 1;
        String message = "Hello, this is a test message.";
        DatabaseController.saveSentMessage(chatId, message);
        List<Message> messages = DatabaseController.getMessages(chatId);
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0).getContent());
    }

    @Test
    void testSaveReceivedMessage() {
        int chatId = 1;
        String message = "Hello, this is a test message.";
        DatabaseController.saveReceivedMessage(chatId, message);
        List<Message> messages = DatabaseController.getMessages(chatId);
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0).getContent());
    }

    @Test
    void testDeleteUser() {
        User user = new User("testUser", getInetAddress(), true);
        DatabaseController.addUser(user);
        assertTrue(DatabaseController.containsUser(user));
        DatabaseController.deleteUser(user);
        assertFalse(DatabaseController.containsUser(user));
    }

    private InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isTableExists(String tableName) {
        Connection conn = DatabaseController.connect();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, null);

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    private void cleanDatabase() {
        deleteTestUsers();
        deleteTestChatTables();
    }

    private void deleteTestUsers() {
        Connection conn = DatabaseController.connect();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Users WHERE username LIKE 'testUser%';");
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

    private void deleteTestChatTables() {
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
