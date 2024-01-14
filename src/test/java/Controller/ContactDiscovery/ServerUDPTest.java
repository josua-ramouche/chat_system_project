package Controller.ContactDiscovery;

import Controller.Database.DatabaseController;
import Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static Controller.Database.DatabaseController.deleteUser;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ServerUDPTest {

    @BeforeAll
    static void ensure_database_is_created() {
        DatabaseController.createUserTable();
    }

    @AfterEach
    void clearDatabase() {
        List<User> users = DatabaseController.getAllUsers();
        users.forEach(u -> {
            if (u.getUsername().equals("TestUser1") || u.getUsername().equals("TestUser2") || u.getUsername().equals("TestUser3") || u.getUsername().equals("NewUsername")) {
                deleteUser(u);
            }
        });

    }

    @Test
    void testEchoServer_SendIP() throws IOException {
        String message = "test";
        InetAddress ip_address = mock(InetAddress.class);
        DatagramSocket socket = mock(DatagramSocket.class);

        assertDoesNotThrow(() -> ServerUDP.EchoServer.sendIP(message,ip_address,socket));

        ArgumentCaptor<DatagramPacket> packetCaptor = ArgumentCaptor.forClass(DatagramPacket.class);
        Mockito.verify(socket,times(1)).send(packetCaptor.capture());

        DatagramPacket sentPacket = packetCaptor.getValue();
        assertEquals(message, new String(sentPacket.getData()));
        assertEquals(ip_address, sentPacket.getAddress());
        assertEquals(1556, sentPacket.getPort());
    }

    @Test
    void testEchoServer_HandleBroadcastMessage() throws IOException {
        User testUser = new User("Test",InetAddress.getLoopbackAddress());
        DatagramSocket datagramSocket = mock(DatagramSocket.class);

        ServerUDP.EchoServer echoServer = new ServerUDP.EchoServer(testUser,datagramSocket);

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

        // Actual list obtained from tested method
        List<User> contactList = DatabaseController.getUsers();

        // Expected list of user
        List<User> expectedList = new ArrayList<>();

        expectedList.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));
        expectedList.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));
        expectedList.add(new User("TestUser3",InetAddress.getByName("192.168.0.3"),true));

        // Comparison of expected list and actual list
        assertEquals(expectedList.size(),contactList.size());
        for(int i=0;i<expectedList.size();i++) {
            assertEquals(expectedList.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList.get(i).getState(), contactList.get(i).getState());
        }
    }

    @Test
    void testEchoServer_HandleResponseMessage() throws IOException {
        User testUser = new User("Test",InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerUDP.EchoServer echoServer = new ServerUDP.EchoServer(testUser, socket);

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

        // Actual list obtained from response messages from server (connected user)
        List<User> contactList = DatabaseController.getUsers();
        for (User user : contactList) {
            System.out.println("Username : "+ user.getUsername() + " / IPAddress : "+ user.getIPAddress() + " / connectionState / " + user.getState());
        }

        // Expected list after response messages sent by server (connected user)
        List<User> expectedList = new ArrayList<>();

        expectedList.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));
        expectedList.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));
        expectedList.add(new User("TestUser3",InetAddress.getByName("192.168.0.3"),true));

        // Comparison of expected list and actual list
        assertEquals(expectedList.size(),contactList.size());
        for(int i=0;i<expectedList.size();i++) {
            assertEquals(expectedList.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList.get(i).getState(), contactList.get(i).getState());
        }
    }

    @Test
    void testEchoServer_HandleEndMessage() throws IOException {
        User testUser = new User("Test", InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerUDP.EchoServer echoServer = new ServerUDP.EchoServer(testUser, socket);

        // Simulate broadcast messages
        String broadcastMessage1 = "BROADCAST:TestUser1";
        InetAddress senderAddress1 = InetAddress.getByName("192.168.0.1");

        String broadcastMessage2 = "BROADCAST:TestUser2";
        InetAddress senderAddress2 = InetAddress.getByName("192.168.0.2");

        echoServer.handleBroadcastMessage(broadcastMessage1.substring("BROADCAST:".length()), senderAddress1);
        echoServer.handleBroadcastMessage(broadcastMessage2.substring("BROADCAST:".length()), senderAddress2);

        // Actual list obtained after first broadcast messages
        List<User> contactList = DatabaseController.getUsers();

        // Expected list after first broadcast messages
        List<User> expectedList1 = new ArrayList<>();

        expectedList1.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));
        expectedList1.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));

        // Comparison of expected list and actual list after first broadcast messages
        assertEquals(expectedList1.size(),contactList.size());
        for(int i=0;i<expectedList1.size();i++) {
            assertEquals(expectedList1.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList1.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList1.get(i).getState(), contactList.get(i).getState());
        }

        // Simulate an end message
        echoServer.handleEndMessage(senderAddress1);

        // Actual list obtained after disconnection of a user
        contactList = DatabaseController.getUsers();

        // Expected list after disconnection of a user (State of connection = false)
        List<User> expectedList2 = new ArrayList<>();
        expectedList2.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));

        // Comparison of expected list and actual list after a user disconnects
        assertEquals(expectedList2.size(),contactList.size());
        for(int i=0;i<expectedList2.size();i++) {
            assertEquals(expectedList2.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList2.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList2.get(i).getState(), contactList.get(i).getState());
        }
    }

    @Test
    void testEchoServer_HandleChangeUsernameMessage() throws IOException {
        User testUser = new User("Test", InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerUDP.EchoServer echoServer = new ServerUDP.EchoServer(testUser,socket);

        // Simulate broadcast messages
        String broadcastMessage1 = "BROADCAST:TestUser1";
        InetAddress senderAddress1 = InetAddress.getByName("192.168.0.1");

        String broadcastMessage2 = "BROADCAST:TestUser2";
        InetAddress senderAddress2 = InetAddress.getByName("192.168.0.2");

        echoServer.handleBroadcastMessage(broadcastMessage1.substring("BROADCAST:".length()), senderAddress1);
        echoServer.handleBroadcastMessage(broadcastMessage2.substring("BROADCAST:".length()), senderAddress2);

        // Actual list obtained after first broadcast messages
        List<User> contactList = DatabaseController.getUsers();

        // Expected list after first broadcast messages
        List<User> expectedList1 = new ArrayList<>();

        expectedList1.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));
        expectedList1.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));

        // Comparison of expected list and actual list after first broadcast messages
        assertEquals(expectedList1.size(),contactList.size());
        for(int i=0;i<expectedList1.size();i++) {
            assertEquals(expectedList1.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList1.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList1.get(i).getState(), contactList.get(i).getState());
        }

        // Simulate a change username message
        String changeUsernameMessage = "CHANGE_USERNAME:TestUser1:NewUsername";

        echoServer.handleChangeUsernameMessage(changeUsernameMessage, senderAddress1);
        echoServer.handleChangeUsernameMessage(changeUsernameMessage, senderAddress2);

        contactList = DatabaseController.getUsers();

        // Expected list after TestUser1 changes name to NewUsername
        List<User> expectedList2 = new ArrayList<>();

        expectedList2.add(new User("NewUsername",InetAddress.getByName("192.168.0.1"),true));
        expectedList2.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));

        // Comparison of expected list with actual list after change of name
        assertEquals(expectedList2.size(),contactList.size());
        for(int i=0;i<expectedList2.size();i++) {
            assertEquals(expectedList2.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList2.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList2.get(i).getState(), contactList.get(i).getState());
        }
    }

    @Test
    void testEchoServer_isUsernameUniqueUsernameAddress() throws UnknownHostException {
        User testUser = new User("Test", InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerUDP.EchoServer echoServer = new ServerUDP.EchoServer(testUser,socket);

        DatabaseController.addUser(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));

        assertTrue(echoServer.isUsernameUnique("TestUser1",InetAddress.getByName("192.168.0.1")));
        assertFalse(echoServer.isUsernameUnique("TestUser1",InetAddress.getByName("192.168.0.3")));
        assertTrue(echoServer.isUsernameUnique("TestUser2",InetAddress.getByName("192.168.0.1")));
        assertTrue(echoServer.isUsernameUnique("TestUser2",InetAddress.getByName("192.168.0.2")));
    }

    @Test
    void testEchoServer_isUsernameUniqueUsername() throws UnknownHostException {
        User testUser = new User("Test", InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerUDP.EchoServer echoServer = new ServerUDP.EchoServer(testUser,socket);

        DatabaseController.addUser(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));

        assertFalse(echoServer.isUsernameUnique("TestUser1"));
        assertTrue(echoServer.isUsernameUnique("TestUser2"));
    }

}