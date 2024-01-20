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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest {

    private static final List<String> createdChatTables = new ArrayList<>();

    //Ensure that a table "Users" is created, and that all users are initially disconnected
    @BeforeAll
    static void setup() {
        DatabaseController.initConnection();
    }

    //Start each test method with a clean database, and empty tables
    @BeforeEach
    void beforeEach() {
        cleanDatabase();
        DatabaseController.createUserTable();
        int chatID = Integer.MAX_VALUE;
        DatabaseController.createChatTable(chatID);
        createdChatTables.add("Chat" + chatID);
    }

    //End each test method by removing test objects from the database
    @AfterEach
    void afterEach() {
        cleanDatabase();
    }

    @Test
    void createUserTableTest() {
        DatabaseController.createUserTable();
        //Check that table "Users" was created in database
        assertTrue(isTableExists("Users"));
    }

    @Test
    void createChatTableTest() {
        int chatId = 1;
        DatabaseController.createChatTable(chatId);
        //Check that table "Chat1" was created in database
        assertTrue(isTableExists("Chat" + chatId));
        createdChatTables.add("Chat1");
    }

    @Test
    void initConnectionTest() {
        DatabaseController.initConnection();
        List<User> users = DatabaseController.getUsers();
        //Check that all users in database are disconnected
        assertTrue(users.isEmpty());
    }

    @Test
    void addUserTest() {
        User user = new User("testUser", getInetAddress(), true);
        DatabaseController.addUser(user);
        //Check that the user was added in the database
        assertTrue(DatabaseController.containsUser(user));
    }

    @Test
    void updateUsernameTest() {
        User user = new User("testUser", getInetAddress(), true);
        DatabaseController.addUser(user);
        String newUsername = "updatedUser";
        DatabaseController.updateUsername(user, newUsername);
        User updatedUser = DatabaseController.getUser(DatabaseController.getUserID(user));
        //Check that the username was changed in the database
        assertEquals(newUsername, updatedUser.getUsername());
    }

    @Test
    void updateConnectionStateTest() {
        User user = new User("testUser", getInetAddress(), true);
        DatabaseController.addUser(user);
        boolean newState = false;
        DatabaseController.updateConnectionState(user, newState);
        User updatedUser = DatabaseController.getUser(DatabaseController.getUserID(user));
        //Check that the connection state was updated in the database
        assertEquals(newState, updatedUser.getState());
    }

    @Test
    void containsUserTest() {
        User user = new User("testUser", getInetAddress(), true);
        //Check that the database does not contain the user
        assertFalse(DatabaseController.containsUser(user));
        DatabaseController.addUser(user);
        //Check that the database contains the user
        assertTrue(DatabaseController.containsUser(user));
    }

    @Test
    void getUsersTest() {
        List<User> users = DatabaseController.getUsers();
        assertNotNull(users);
    }

    @Test
    void getAllUsersTest() {
        List<User> users = DatabaseController.getAllUsers();
        assertNotNull(users);
    }

    @Test
    void saveSentMessageTest() {
        int chatId = Integer.MAX_VALUE;
        String message = "Hello, this is a test message.";
        DatabaseController.saveSentMessage(chatId, message);
        List<Message> messages = DatabaseController.getMessages(chatId);
        //Check that a message was added in the database, and that this message correspond to the message we created
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0).getContent());
    }

    @Test
    void saveReceivedMessageTest() {
        int chatId = Integer.MAX_VALUE;
        String message = "Hello, this is a test message.";
        DatabaseController.saveReceivedMessage(chatId, message);
        List<Message> messages = DatabaseController.getMessages(chatId);
        //Check that a message was added in the database, and that this message correspond to the message we created
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0).getContent());
    }

    @Test
    void deleteUserTest() {
        User user = new User("testUser", getInetAddress(), true);
        DatabaseController.addUser(user);
        //Check that a user was added to the database
        assertTrue(DatabaseController.containsUser(user));
        DatabaseController.deleteUser(user);
        //Check that there is no more users in database after removing the only user
        assertFalse(DatabaseController.containsUser(user));
    }

    //Used to get the local IP address for test purposes
    private InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Check that a table exist in our database
    private boolean isTableExists(String tableName) {
        Connection conn = DatabaseController.connect();
        try {
            DatabaseMetaData data = conn.getMetaData();
            ResultSet resultSet = data.getTables(null, null, tableName, null);

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

    //Delete all test users from the database
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

    //Delete all test chat tables from the database
    private void deleteTestChatTables() {
        Connection conn = DatabaseController.connect();
        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                if (tableName.startsWith("Chat")) {
                    if (createdChatTables.contains(tableName)) {
                        // Delete only tables created during the test
                        stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName + ";");
                    }                }
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
