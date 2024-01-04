package Controller.ContactDiscovery;

import Controller.ContactDiscovery.ServerUDP;
import Model.ContactList;
import Model.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.*;

class ServerUDPTest {


    void deleteFromDatabase(User u) {
        String sql = "DELETE FROM Users WHERE username = ?;";

    }

    @Test
    void testEchoServer_HandleBroadcastMessage() throws IOException {
        User testUser = new User("Test",InetAddress.getLoopbackAddress());
        DatagramSocket socket = mock(DatagramSocket.class);

        ServerUDP.EchoServer echoServer = new ServerUDP.EchoServer(testUser,socket);

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
        List<User> contactList = ContactList.getContacts();

        // Expected list of user
        List<User> expectedList = new ArrayList<>();

        expectedList.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));
        expectedList.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));
        expectedList.add(new User("TestUser3",InetAddress.getByName("192.168.0.3"),true));
        expectedList.add(new User("TestUser4",InetAddress.getByName("192.168.0.4"),true));

        // Comparison of expected list and actual list
        for(int i=0;i<expectedList.size()-1;i++) {
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
        List<User> contactList = ContactList.getContacts();
        for (User user : contactList) {
            System.out.println("Username : "+ user.getUsername() + " / IPAddress : "+ user.getIPAddress() + " / connectionState / " + user.getState());
        }

        // Expected list after response messages sent by server (connected user)
        List<User> expectedList = new ArrayList<>();

        expectedList.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));
        expectedList.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));
        expectedList.add(new User("TestUser3",InetAddress.getByName("192.168.0.3"),true));

        // Comparison of expected list and actual list
        for(int i=0;i<expectedList.size()-1;i++) {
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
        List<User> contactList = ContactList.getContacts();

        // Expected list after first broadcast messages
        List<User> expectedList1 = new ArrayList<>();

        expectedList1.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));
        expectedList1.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));

        // Comparison of expected list and actual list after first broadcast messages
        for(int i=0;i<expectedList1.size()-1;i++) {
            assertEquals(expectedList1.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList1.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList1.get(i).getState(), contactList.get(i).getState());
        }

        // Simulate an end message
        echoServer.handleEndMessage(senderAddress1);

        // Actual list obtained after disconnection of a user
        contactList = ContactList.getContacts();

        // Expected list after disconnection of a user (State of connection = false)
        List<User> expectedList2 = new ArrayList<>();
        expectedList2.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),false));
        expectedList2.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));

        // Comparison of expected list and actual list after a user disconnects
        for(int i=0;i<expectedList1.size()-1;i++) {
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
        List<User> contactList = ContactList.getContacts();

        // Expected list after first broadcast messages
        List<User> expectedList1 = new ArrayList<>();

        expectedList1.add(new User("TestUser1",InetAddress.getByName("192.168.0.1"),true));
        expectedList1.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));

        // Comparison of expected list and actual list after first broadcast messages
        for(int i=0;i<expectedList1.size()-1;i++) {
            assertEquals(expectedList1.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList1.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList1.get(i).getState(), contactList.get(i).getState());
        }

        // Simulate a change username message
        String changeUsernameMessage = "CHANGE_USERNAME:TestUser1:NewUsername";

        echoServer.handleChangeUsernameMessage(changeUsernameMessage, senderAddress1);
        echoServer.handleChangeUsernameMessage(changeUsernameMessage, senderAddress2);

        // Expected list after TestUser1 changes name to NewUsername
        List<User> expectedList2 = new ArrayList<>();

        expectedList2.add(new User("NewUsername",InetAddress.getByName("192.168.0.1"),true));
        expectedList2.add(new User("TestUser2",InetAddress.getByName("192.168.0.2"),true));

        // Comparison of expected list with actual list after change of name
        for(int i=0;i<expectedList1.size()-1;i++) {
            assertEquals(expectedList2.get(i).getUsername(), contactList.get(i).getUsername());
            assertEquals(expectedList2.get(i).getIPAddress(), contactList.get(i).getIPAddress());
            assertEquals(expectedList2.get(i).getState(), contactList.get(i).getState());
        }

    }


}