package Controller.Chat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.Socket;
public class ServerTCPTest {
    @Test
    void clientHandlerRunTest() throws IOException {
        // Redirect System.out to capture the output
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));

        // Mock the static method of Socket class
        Socket fakeSocket = Mockito.mock(Socket.class);
        Mockito.when(fakeSocket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        Mockito.when(fakeSocket.getInputStream()).thenReturn(new ByteArrayInputStream("Test Message\n".getBytes()));

        // Create a new client handler with the fake socket
        ServerTCP.ClientHandler clientHandler = new ServerTCP.ClientHandler(fakeSocket);

        // Start the client handler
        clientHandler.start();

        // Sleep to allow the client handler to process the message
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Restore the original System.out
        System.setOut(System.out);

        // Assert that the client handler processed the message
        // Add more assertions based on your specific logic
        // Here, we are just checking if the message was printed
        // to the console.
        assertTrue(capturedOutput.toString().contains("Received: Test Message"));
    }

    @Test
    void endConnectionTest() throws IOException {
        // Mock the static method of Socket class
        Socket fakeSocket = Mockito.mock(Socket.class);

        // Create a new client handler with the fake socket
        ServerTCP.ClientHandler clientHandler = new ServerTCP.ClientHandler(fakeSocket);

        // End the connection
        ServerTCP.ClientHandler.endConnection();

        // Assert that the socket is closed
        Mockito.verify(fakeSocket);
        fakeSocket.close();
        Mockito.verifyNoMoreInteractions(fakeSocket);
    }
}
