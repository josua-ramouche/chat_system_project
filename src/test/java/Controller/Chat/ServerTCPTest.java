package Controller.Chat;

import Controller.Database.DatabaseController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ServerTCPTest {

    @BeforeAll
    static void setup() {
        DatabaseController.initConnection();
    }

    @AfterEach
    void afterEach() {
        cleanDatabase();
    }

    @Test
    void listenTCPTest(){
        // Check if no exception were thrown during the test
        try {
            // Signal the thread to stop after the test
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

            //To be sure that the server starts
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

            assertNotNull(mockClientSocket);
        } catch(Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    void testClientHandlerRun() {
        // Check if no exception were thrown during the test
        try {
            // Create a mock ServerSocket
            ServerSocket serverSocket = new ServerSocket(0);

            // Create a mock Socket for testing and connect it to the ServerSocket
            Socket mockSocket = new Socket("localhost", serverSocket.getLocalPort());

            // Create a mock ServerTCP.ClientHandler instance
            ServerTCP.ClientHandler clientHandler = new ServerTCP.ClientHandler(mockSocket, null);

            assertNotNull(clientHandler);

            // Close the mock Socket and ServerSocket
            mockSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    //Delete all test chat tables from the database
    private void cleanDatabase() {
        Connection conn = DatabaseController.connect();
        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                if (tableName.startsWith("Chat")) {
                        stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName + ";");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
