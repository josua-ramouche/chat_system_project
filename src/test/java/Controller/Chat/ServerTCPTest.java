package Controller.Chat;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import Controller.Chat.ServerTCP;
import Controller.Database.DatabaseController;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerTCPTest {

    @Test
    void testListenTCP() throws IOException, InterruptedException {
        // Use an AtomicBoolean to signal the thread to stop after the test
        AtomicBoolean stopThread = new AtomicBoolean(false);

        // Start the server thread
        Thread serverThread = new Thread(() -> {
            ServerTCP.listenTCP server = new ServerTCP.listenTCP();
            server.start();
            while (!stopThread.get()) {
                // Wait for the server to finish
            }
        });
        serverThread.start();

        // Mock client socket
        Socket mockClientSocket = new Socket("localhost", 1556);

        // Send a message to the server to trigger a response
        try (PrintWriter out = new PrintWriter(mockClientSocket.getOutputStream(), true)) {
            out.println("Test message");
        }

        // Stop the server thread
        stopThread.set(true);
        serverThread.join();

        // TODO: Add assertions to verify the expected behavior of listenTCP
        assertNotNull(mockClientSocket);
        // Add more assertions as needed
    }

    @Test
    void testClientHandlerRun() throws IOException {
        // Create a mock Socket for testing
        Socket mockSocket = mock(Socket.class);

        // Create a mock BufferedReader for testing
        BufferedReader mockBufferedReader = mock(BufferedReader.class);
        InputStream inputStream = new ByteArrayInputStream("Test message".getBytes());
        InputStreamReader mockInputStreamReader = new InputStreamReader(inputStream);

        when(mockSocket.getInputStream()).thenReturn(inputStream);
        when(mockSocket.getOutputStream()).thenReturn(System.out);

        // Call the method to create the chat table for testing
        DatabaseController.createChatTableForTest(0);

        // Add an assertion to check if the table exists
        assertTrue(DatabaseController.doesTableExist("Chat0"));

        // Create a mock ServerTCP.ClientHandler instance
        ServerTCP.ClientHandler clientHandler = new ServerTCP.ClientHandler(mockSocket, mock(Socket.class).getInetAddress());

        // Add assertions to verify the expected behavior of ClientHandler's run method
        assertNotNull(clientHandler);

        // For example, you might add assertions to check the behavior of the run method
        // assertEquals(expectedValue, actualValue);
    }


}
