package Controller.Chat;

import Controller.Database.DatabaseController;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientTCPTest {
    private static final int TEST_PORT = 54321;
    private static final String TEST_IP = "127.0.0.1";
    private static ServerSocket serverSocket;

    @BeforeEach
    void initSocket() {
        try {
            serverSocket = new ServerSocket(TEST_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void cleanup() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void afterEach() {
        cleanDatabase();
    }
    @Test
    public void testStartConnection() {
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(TEST_IP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientTCP.startConnection(ip, TEST_PORT);

        //Check that variables out and in used for sending and receiving message were initialized
        assertNotNull(ClientTCP.getOut());
        assertNotNull(ClientTCP.getIn());
    }


    @Test
    public void testSendMessage() throws IOException {
        InetAddress ip = InetAddress.getByName(TEST_IP);
        ClientTCP.startConnection(ip, TEST_PORT);

        Socket testSocket = serverSocket.accept();
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));

        try {
            String message = "Hello, Server!";
            ClientTCP.sendMessage(message);

            //Check that sent message was received
            assertEquals(message, socketIn.readLine());
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // rethrow the exception to see the stack trace
        }
    }

    //Test for the removeIP(), setIPList() and getIPList() methods
    @Test
    public void testRemoveIPAndSetGetIPList() {

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(TEST_IP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<InetAddress> ipList = new ArrayList<>();
        ipList.add(ip);

        ClientTCP clientTest = new ClientTCP();

        clientTest.setIPList(ipList);

        //Check that the ip was added to ipList
        assertEquals(1,ClientTCP.getIPList().size());

        ClientTCP.removeIP(ip);
        //Check that the ip was removed to ipList
        assertEquals(0,ClientTCP.getIPList().size());
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
