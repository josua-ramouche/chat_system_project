package Model;

import org.junit.jupiter.api.Test;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testSetAndGetUsername() {
        User user = new User();
        // Test setter
        user.setUsername("TestUser");
        // Test getter
        assertEquals("TestUser", user.getUsername());

    }

    @Test
    void testSetAndGetIPaddress() throws UnknownHostException {
        User user = new User();
        // Test setter
        user.setIPAddress(InetAddress.getByName("192.168.0.1"));
        // Test getter
        assertEquals(InetAddress.getByName("192.168.0.1"), user.getIPAddress());
    }

    @Test
    void testSetAndGetState() {
        User user = new User();
        // Test setter
        user.setState(true);
        // Test getter
        assertTrue(user.getState());
    }


}