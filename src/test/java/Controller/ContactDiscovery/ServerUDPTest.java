package Controller.ContactDiscovery;

import Model.ContactList;
import Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerUDPTest {

    private ServerUDP.EchoServer echoServer;

    @BeforeEach
    public void setup() throws SocketException, UnknownHostException {
        User serverUser = new User("ServerUser", InetAddress.getLocalHost(), true);
        echoServer = new ServerUDP.EchoServer(serverUser, TestUtils.createMockSocket());
    }

    @Test
    public void testHandleBroadcastMessage() {
        // Create a mock broadcast message
        String broadcastMessage = "BROADCAST:NewUser";

        // Mock sender's address
        InetAddress senderAddress = TestUtils.createMockAddress("192.168.1.2");

        // Mock the broadcast message handling
        echoServer.handleBroadcastMessage(broadcastMessage, senderAddress);

        // Assert that the contact list is updated
        assertTrue(ContactList.getContacts().size() > 0);

        // Assert that the response message is sent to the sender
        assertEquals("HANDLE_RESPONSE_MESSAGE:ServerUser", TestUtils.getLastSentMessage());
    }

    @Test
    public void testHandleEndMessage() {
        // Mock sender's address
        InetAddress senderAddress = TestUtils.createMockAddress("192.168.1.2");

        // Mock the end message handling
        echoServer.handleEndMessage(senderAddress);

        // Assert that the contact list is updated
        assertTrue(ContactList.getContacts().isEmpty());
    }

    @Test
    public void testHandleChangeUsernameMessage() {
        // Create a mock change username message
        String changeUsernameMessage = "CHANGE_USERNAME:OldUser:NewUser";

        // Mock sender's address
        InetAddress senderAddress = TestUtils.createMockAddress("192.168.1.2");

        // Mock the change username message handling
        echoServer.handleChangeUsernameMessage(changeUsernameMessage, senderAddress);

        // Assert that the username is changed in the contact list
        assertEquals("NewUser", ContactList.getContacts().get(0).getUsername());
    }

    // Add more test methods for other functionalities of ServerUDP.EchoServer

    // Helper class for mocking DatagramSocket and capturing sent messages
    static class TestUtils {
        private static String lastSentMessage;
        private static DatagramSocket mockSocket;

        static DatagramSocket createMockSocket() throws SocketException {
            mockSocket = new DatagramSocket();
            return mockSocket;
        }

        static InetAddress createMockAddress(String ipAddress) {
            try {
                return InetAddress.getByName(ipAddress);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        static void setLastSentMessage(String message) {
            lastSentMessage = message;
        }

        static String getLastSentMessage() {
            return lastSentMessage;
        }
    }
}
