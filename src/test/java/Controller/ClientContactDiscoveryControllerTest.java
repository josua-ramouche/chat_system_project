package Controller;

import Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.net.SocketException;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ClientContactDiscoveryControllerTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp(){
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void broadcast() {
        try{
            ClientContactDiscoveryController.broadcast("Test",InetAddress.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void listAllBroadcastAddresses() {
        try {
            List<InetAddress> broadcastList = ClientContactDiscoveryController.listAllBroadcastAddresses();
            assertNotNull(broadcastList);
            assertFalse(broadcastList.isEmpty());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Test
    void sendUniqueUsername() throws UnknownHostException {
        User testUser = mock(User.class);
        User contact = new User("Unique", InetAddress.getLoopbackAddress());
        List<User> testContactList = new ArrayList<>();
        testContactList.add(contact);

        List<InetAddress> broadcastList = new ArrayList<>();
        try{
            broadcastList.add(InetAddress.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientContactDiscoveryController.sendUsername(broadcastList,testUser);

        assertEquals("Broadcast address : " + InetAddress.getLocalHost() + "\n", outputStreamCaptor.toString());
    }

    @Test
    void sendNonUniqueUsername() {
        User testUser = new User("User1",InetAddress.getLoopbackAddress());
        User contact1 = new User("User1", InetAddress.getLoopbackAddress());
        User contact2 = new User("User2", InetAddress.getLoopbackAddress());
        List<User> testContactList = new ArrayList<>();
        testContactList.add(contact1);
        testContactList.add(contact2);

        testUser.setContactList(testContactList);

        List<InetAddress> broadcastList = new ArrayList<>();
        try{
            broadcastList.add(InetAddress.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientContactDiscoveryController.sendUsername(broadcastList,testUser);
        assertEquals("Username 'User1' is not unique. Please choose a different username.\r\n", outputStreamCaptor.toString());
    }

    @Test
    void sendChangeUsername() {
        User testUser = new User("TestUser", InetAddress.getLoopbackAddress());
        testUser.addContact(new User("ContactUser", InetAddress.getLoopbackAddress(), true));
        ClientContactDiscoveryController.sendChangeUsername(testUser, "NewUsername");
        assertEquals("Username changed to : NewUsername\n", outputStreamCaptor.toString());
    }

    @Test
    void sendEndConnection() {
        User testUser = new User("TestUser", InetAddress.getLoopbackAddress(), true);
        testUser.addContact(new User("ContactUser", InetAddress.getLoopbackAddress(), true));
        ClientContactDiscoveryController.sendEndConnection(testUser);
        assertEquals("Disconnection...\nYou are now disconnected\n", outputStreamCaptor.toString());
    }
}