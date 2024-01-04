package Model;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactListTest {

    private User testUser1;
    private User testUser2;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws UnknownHostException {
        // Initialize test data before each test
        testUser1 = new User("JohnDoe", InetAddress.getLocalHost(), true);
        testUser2 = new User("JaneDoe", InetAddress.getLocalHost(), true);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Clear the contact list and reset System.out after each test
        ContactList.getContacts().clear();
        System.setOut(originalOut);
    }

    @Test
    void addContact() {
        ContactList.addContact(testUser1);
        assertTrue(ContactList.getContacts().contains(testUser1));
    }

    @Test
    void deleteContact() {
        ContactList.addContact(testUser1);
        ContactList.addContact(testUser2);

        ContactList.deleteContact(testUser1);
        assertTrue(ContactList.getContacts().contains(testUser2));
        assertEquals(1, ContactList.getContacts().size());
    }

    @Test
    void printContactList() {
        ContactList.addContact(testUser1);
        ContactList.addContact(testUser2);

        ContactList.printContactList();

        String printedOutput = outContent.toString().trim();
        assertTrue(printedOutput.contains("Print Contact List"));
        assertTrue(printedOutput.contains("user : " + testUser1.getUsername()));
        assertTrue(printedOutput.contains("user : " + testUser2.getUsername()));
    }
}
