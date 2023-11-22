package Controller;
import Model.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientContactDiscoveryControllerTest {

    @Test
    void broadcast() {
        // Test broadcast method
        try {
            ClientContactDiscoveryController.broadcast("Test Message", InetAddress.getLocalHost());
        } catch (IOException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void listAllBroadcastAddresses() {
        // Test listAllBroadcastAddresses method
        try {
            List<InetAddress> broadcastAddresses = ClientContactDiscoveryController.listAllBroadcastAddresses();
            assertNotNull(broadcastAddresses);
            // You can add more specific assertions about the expected broadcast addresses if needed
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
            broadcastList = ClientContactDiscoveryController.listAllBroadcastAddresses();
            ClientContactDiscoveryController.sendUsername(broadcastList, user);

            // Verify that the contact list is empty before sending the first broadcast to check the unicity of the username
            assertEquals(0, user.getContactList().size());
        } catch (IOException e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void sendChangeUsername() {
        // Test sendChangeUsername method
        User user = new User("TestUser", InetAddress.getLoopbackAddress(), true);
        try {
            ClientContactDiscoveryController.sendChangeUsername(user, "NewUsername");

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
            ClientContactDiscoveryController.sendEndConnection(user);

            // Verify that the contact list is empty and user state is false after disconnection
            assertTrue(user.getContactList().isEmpty());
            assertFalse(user.getState());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }
}
