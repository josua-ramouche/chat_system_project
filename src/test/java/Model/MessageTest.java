package Model;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void testMessageConstructor() {
        User sender = new User("senderUsername", InetAddress.getLoopbackAddress(), true);
        Message message = new Message("Hello, World!", "2024-01-04", sender);

        assertEquals("Hello, World!", message.getContent());
        assertEquals("2024-01-04", message.getDate());
        assertEquals(sender, message.getSender());
    }

    @Test
    void testMessageGetters() {
        User sender = new User("senderUsername", InetAddress.getLoopbackAddress(), true);
        Message message = new Message("Hello, World!", "2024-01-04", sender);

        assertEquals("Hello, World!", message.getContent());
        assertEquals("2024-01-04", message.getDate());
        assertEquals(sender, message.getSender());
    }


}
