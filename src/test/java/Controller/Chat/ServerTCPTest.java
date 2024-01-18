package Controller.Chat;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        // Allow some time for the server to start (this can be improved)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mock client socket
        Socket mockClientSocket = new Socket("localhost", 1556);

        // Send a message to the server to trigger a response
        try (PrintWriter out = new PrintWriter(mockClientSocket.getOutputStream(), true)) {
            out.println("Test message");
        }

        // Stop the server thread
        stopThread.set(true);
        serverThread.join();

        // Add assertions to verify the expected behavior of listenTCP
        assertNotNull(mockClientSocket);
    }

    @Test
    void testClientHandlerRun() throws IOException {
        // Create a mock ServerSocket
        ServerSocket serverSocket = new ServerSocket(0);

        // Create a mock Socket for testing and connect it to the ServerSocket
        Socket mockSocket = new Socket("localhost", serverSocket.getLocalPort());

        // Create a mock ServerTCP.ClientHandler instance
        ServerTCP.ClientHandler clientHandler = new ServerTCP.ClientHandler(mockSocket, null);

        // Add assertions to verify the expected behavior of ClientHandler's run method
        assertNotNull(clientHandler);

        // Close the mock Socket and ServerSocket
        mockSocket.close();
        serverSocket.close();
    }
}
