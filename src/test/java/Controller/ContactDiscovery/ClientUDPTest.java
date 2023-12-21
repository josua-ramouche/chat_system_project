package Controller.ContactDiscovery;
import Controller.ContactDiscovery.ClientUDP;
import Model.ContactList;
import Model.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientUDPTest {

    @Test
    void broadcast() {
        // Test broadcast method
        try {
            ClientUDP.broadcast("Test Message", InetAddress.getLocalHost());
        } catch (IOException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void listAllBroadcastAddresses() {
        // Test listAllBroadcastAddresses method
        try {
            List<InetAddress> broadcastAddresses = ClientUDP.listAllBroadcastAddresses();
            assertNotNull(broadcastAddresses);
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void sendUsername() {
        // Test sendUsername method
        User user = new User("TestUser", InetAddress.getLoopbackAddress(), true);
        List<InetAddress> broadcastList;
        try {
            broadcastList = ClientUDP.listAllBroadcastAddresses();
            ClientUDP.sendUsername(broadcastList, user);

            // Verify that the contact list is empty before sending the first broadcast to check the unicity of the username
            assertEquals(0, ContactList.getContacts().size());
        } catch (IOException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void sendChangeUsername() {
        // Test sendChangeUsername method
        User user = new User("TestUser", InetAddress.getLoopbackAddress(), true);
        try {
            ClientUDP.sendChangeUsername(user, "NewUsername");

            // Verify that the username is changed as expected
            assertEquals("NewUsername", user.getUsername());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void sendEndConnection() {
        // Test sendEndConnection method
        User user = new User("TestUser", InetAddress.getLoopbackAddress(), true);
        try {
            ClientUDP.sendEndConnection(user);

            // Verify that the contact list is empty and user state is false after disconnection
            assertTrue(ContactList.getContacts().isEmpty());
            assertFalse(user.getState());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }
}
