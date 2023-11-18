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
        user.setIPaddress(InetAddress.getByName("192.168.0.1"));
        // Test getter
        assertEquals(InetAddress.getByName("192.168.0.1"), user.getIPaddress());
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
    void testSetAndGetContactList() throws UnknownHostException {
        User user = new User();
        List<User> contactList = new ArrayList<>();

        User contact1 = new User("Josua",InetAddress.getLoopbackAddress());
        User contact2 = new User("Lucile", InetAddress.getByName("192.168.0.1"));

        contactList.add(contact1);
        contactList.add(contact2);

        // Test setter
        user.setContactList(contactList);
        // Test getter
        assertEquals(user.getContactList().size(),2);
        assertEquals(user.getContactList().get(0),contact1);
        assertEquals(user.getContactList().get(1),contact2);
    }

    @Test
    void testAddContact() throws UnknownHostException {
        User user = new User();
        User contact = new User("ContactUser", InetAddress.getByName("192.168.0.2"), true);

        // Add contact
        user.addContact(contact);

        // Check if the contact is added to the contact list
        assertTrue(user.getContactList().contains(contact));
    }

    @Test
    void testDeleteContact() throws UnknownHostException {
        User user = new User();
        User contact = new User("ContactUser", InetAddress.getByName("192.168.0.2"), true);

        // Add contact
        user.addContact(contact);

        // Delete contact
        user.deleteContact(contact);

        // Check if the contact is removed from the contact list
        assertFalse(user.getContactList().contains(contact));
    }

    @Test
    void testContainsContact() throws UnknownHostException {
        User user = new User();
        User contact = new User("ContactUser", InetAddress.getByName("192.168.0.2"), true);

        // Add contact
        user.addContact(contact);

        // Check if the contact is contained in the contact list
        assertTrue(user.containsContact(user.getContactList(), contact));
    }

    @Test
    void testContainsContact_NotContained() throws UnknownHostException {
        User user = new User();
        User contact = new User("ContactUser", InetAddress.getByName("192.168.0.2"), true);

        // Check if the contact is not contained in an empty contact list
        assertFalse(user.containsContact(user.getContactList(), contact));
    }

}