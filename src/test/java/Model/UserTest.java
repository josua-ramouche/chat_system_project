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

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNull(user.getUsername());
        assertNull(user.getIPAddress());
        assertNull(user.getState());
    }

    @Test
    void testParameterizedConstructor() throws UnknownHostException {
        User user = new User("TestUser", InetAddress.getByName("192.168.0.1"), true);
        assertEquals("TestUser", user.getUsername());
        assertEquals(InetAddress.getByName("192.168.0.1"), user.getIPAddress());
        assertTrue(user.getState());
    }

    @Test
    void testContainsContact() throws UnknownHostException {
        User user1 = new User("User1", InetAddress.getByName("192.168.0.1"), true);
        User user3 = new User("User1", InetAddress.getByName("192.168.0.1"), true);

        List<User> contactList = new ArrayList<>();
        contactList.add(user1);

        assertTrue(user1.containsContact(contactList, user3));
    }


    @Test
    void testHashCode() throws UnknownHostException {
        User user1 = new User("User1", InetAddress.getByName("192.168.0.1"), true);
        User user2 = new User("User1", InetAddress.getByName("192.168.0.1"), true);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

}