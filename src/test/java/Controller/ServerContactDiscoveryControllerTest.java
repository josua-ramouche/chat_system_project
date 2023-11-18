package Controller;

import Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.ByteArrayOutputStream;
import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.*;

class ServerContactDiscoveryControllerTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp(){
        System.setOut(new PrintStream(outputStreamCaptor));
    }
    @Test
    void testEchoServer_HandleBroadcastMessage() throws IOException {
        User testUser = new User("Test",InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerContactDiscoveryController.EchoServer echoServer = new ServerContactDiscoveryController.EchoServer(testUser,socket);

        // Simulate broadcast messages
        String broadcastMessage1 = "BROADCAST:TestUser1";
        InetAddress senderAddress1 = InetAddress.getByName("192.168.0.1");

        String broadcastMessage2 = "BROADCAST:TestUser2";
        InetAddress senderAddress2 = InetAddress.getByName("192.168.0.2");

        String broadcastMessage3 = "BROADCAST:TestUser3";
        InetAddress senderAddress3 = InetAddress.getByName("192.168.0.3");

        String broadcastMessage4 = "BROADCAST:TestUser1";
        InetAddress senderAddress4 = InetAddress.getByName("192.168.0.4");

        echoServer.handleBroadcastMessage(broadcastMessage1.substring("BROADCAST:".length()), senderAddress1);
        echoServer.handleBroadcastMessage(broadcastMessage2.substring("BROADCAST:".length()), senderAddress2);
        echoServer.handleBroadcastMessage(broadcastMessage3.substring("BROADCAST:".length()), senderAddress3);
        echoServer.handleBroadcastMessage(broadcastMessage4.substring("BROADCAST:".length()), senderAddress4);

        assertEquals("New contact added\nContact List (connected):\nTestUser1\n" +
                "New contact added\nContact List (connected):\nTestUser1\nTestUser2\n" +
                "New contact added\nContact List (connected):\nTestUser1\nTestUser2\nTestUser3\n" +
                "Username 'TestUser1' is not unique. Notifying the client.\n", outputStreamCaptor.toString());
    }

    @Test
    void testEchoServer_HandleResponseMessage() throws IOException {
        User testUser = new User("Test",InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerContactDiscoveryController.EchoServer echoServer = new ServerContactDiscoveryController.EchoServer(testUser, socket);

        // Simulate a response message
        String responseMessage1 = "TestUser1";
        InetAddress senderAddress1 = InetAddress.getByName("192.168.0.1");

        String responseMessage2 = "TestUser2";
        InetAddress senderAddress2 = InetAddress.getByName("192.168.0.2");

        String responseMessage3 = "TestUser3";
        InetAddress senderAddress3 = InetAddress.getByName("192.168.0.3");


        echoServer.handleBroadcastMessage(responseMessage1, senderAddress1);
        echoServer.handleBroadcastMessage(responseMessage2, senderAddress2);
        echoServer.handleBroadcastMessage(responseMessage3, senderAddress3);

        assertEquals("New contact added\nContact List (connected):\nTestUser1\n" +
                "New contact added\nContact List (connected):\nTestUser1\nTestUser2\n" +
                "New contact added\nContact List (connected):\nTestUser1\nTestUser2\nTestUser3\n", outputStreamCaptor.toString());

    }

    @Test
    void testEchoServer_HandleEndMessage() throws IOException {
        User testUser = new User("Test", InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerContactDiscoveryController.EchoServer echoServer = new ServerContactDiscoveryController.EchoServer(testUser, socket);

        // Simulate broadcast messages
        String broadcastMessage1 = "BROADCAST:TestUser1";
        InetAddress senderAddress1 = InetAddress.getByName("192.168.0.1");

        String broadcastMessage2 = "BROADCAST:TestUser2";
        InetAddress senderAddress2 = InetAddress.getByName("192.168.0.2");

        echoServer.handleBroadcastMessage(broadcastMessage1.substring("BROADCAST:".length()), senderAddress1);
        echoServer.handleBroadcastMessage(broadcastMessage2.substring("BROADCAST:".length()), senderAddress2);

        // Simulate an end message
        echoServer.handleEndMessage(senderAddress1);

        assertEquals("New contact added\nContact List (connected):\nTestUser1\n" +
                "New contact added\nContact List (connected):\nTestUser1\nTestUser2\n" +
                "User TestUser1 disconnected\nContact List (connected):\nTestUser2\n", outputStreamCaptor.toString());

    }

    @Test
    void testEchoServer_HandleChangeUsernameMessage() throws IOException {
        User testUser = new User("Test", InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerContactDiscoveryController.EchoServer echoServer = new ServerContactDiscoveryController.EchoServer(testUser,socket);

        // Simulate broadcast messages
        String broadcastMessage1 = "BROADCAST:TestUser1";
        InetAddress senderAddress1 = InetAddress.getByName("192.168.0.1");

        String broadcastMessage2 = "BROADCAST:TestUser2";
        InetAddress senderAddress2 = InetAddress.getByName("192.168.0.2");

        echoServer.handleBroadcastMessage(broadcastMessage1.substring("BROADCAST:".length()), senderAddress1);
        echoServer.handleBroadcastMessage(broadcastMessage2.substring("BROADCAST:".length()), senderAddress2);

        // Simulate a change username message
        String changeUsernameMessage = "CHANGE_USERNAME:TestUser1:NewUsername";

        echoServer.handleChangeUsernameMessage(changeUsernameMessage, senderAddress1);
        echoServer.handleChangeUsernameMessage(changeUsernameMessage, senderAddress2);

        assertEquals("New contact added\nContact List (connected):\nTestUser1\n" +
                "New contact added\nContact List (connected):\nTestUser1\nTestUser2\n" +
                "Username changed: TestUser1 to NewUsername\nContact List (connected):\nNewUsername\nTestUser2\n" +
                "Username 'NewUsername' is not unique. Notifying the client.\n", outputStreamCaptor.toString());
    }


}