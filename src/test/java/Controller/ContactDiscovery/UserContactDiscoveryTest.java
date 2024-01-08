package Controller.ContactDiscovery;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class UserContactDiscoveryTest {

    @Test
    void testInituser() {
        try {
            UserContactDiscovery userContactDiscovery = new UserContactDiscovery("testUser");
            UserContactDiscovery.inituser("testUser");

            // Add assertions based on your requirements
            assertEquals("testUser", UserContactDiscovery.temp.getUsername());
            assertNotNull(UserContactDiscovery.temp.getIPAddress());
            assertTrue(UserContactDiscovery.temp.getState());
        } catch (UnknownHostException | SocketException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testInit() {
        try {
            UserContactDiscovery userContactDiscovery = new UserContactDiscovery("testUser");
            ServerUDP.EchoServer echoServer = UserContactDiscovery.Init();

            // Add assertions based on your requirements
            assertNotNull(echoServer);
        } catch (SocketException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testAction() {
        try {
            UserContactDiscovery userContactDiscovery = new UserContactDiscovery("testUser");
            userContactDiscovery.Action();

            // Add assertions based on your requirements
            // You might need to mock certain behaviors for testing
        } catch (UnknownHostException | SocketException | InterruptedException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
