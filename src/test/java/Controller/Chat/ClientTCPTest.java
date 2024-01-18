package Controller.Chat;

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClientTCPTest {
    private static final int TEST_PORT = 54321;  // Utilisez un autre numéro de port disponible
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
    @Test
    public void testStartConnection() {
        // Arrange
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(TEST_IP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act
        ClientTCP.startConnection(ip, TEST_PORT);

        // Assert
        assertNotNull(ClientTCP.getOut());
        assertNotNull(ClientTCP.getIn());
    }


    @Test
    public void testSendMessage() throws IOException {
        // Arrange
        InetAddress ip = InetAddress.getByName(TEST_IP);
        ClientTCP.startConnection(ip, TEST_PORT);

        Socket testSocket = serverSocket.accept();
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));

        // Act
        try {
            String message = "Hello, Server!";
            ClientTCP.sendMessage(message);

            // Assert
            assertEquals(message, socketIn.readLine());
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // rethrow the exception to see the stack trace
        }
    }

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

        assertEquals(1,ClientTCP.getIPList().size());

        ClientTCP.removeIP(ip);
        assertEquals(0,ClientTCP.getIPList().size());
    }


    /*@Test
    public void testStopConnection() {
        // Act
        ClientTCP.stopConnection();

        // Assert
        assertTrue(ClientTCP.socket.isClosed());
    }*/


}
