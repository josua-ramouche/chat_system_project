package Controller.Chat;

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientTCPTest {
    private static final int TEST_PORT = 12345;
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
/*
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
        assertTrue(ClientTCP.socket.isConnected());
    }*/

    @Test
    public void testSendMessage() throws IOException {
        // Arrange
        InetAddress ip = InetAddress.getByName(TEST_IP);
        ClientTCP.startConnection(ip, TEST_PORT);

        Socket testSocket = serverSocket.accept();
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));

        // Act
        String message = "Hello, Server!";
        ClientTCP.sendMessage(message);

        // Assert
        assertEquals(message, socketIn.readLine());
    }

    /*@Test
    public void testStopConnection() {
        // Act
        ClientTCP.stopConnection();

        // Assert
        assertTrue(ClientTCP.socket.isClosed());
    }*/


}
