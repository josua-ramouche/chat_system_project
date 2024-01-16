package Controller.Chat;

import Controller.Database.DatabaseController;
import Model.User;
import View.ChatApp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;


import static Controller.Database.DatabaseController.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ServerTCPTest {

    @BeforeAll
    static void ensure_database_is_created() {
        DatabaseController.createUserTable();
    }

    @AfterEach
    void clearDatabase() {
        List<User> users = DatabaseController.getAllUsers();
        users.forEach(u -> {
            if (u.getUsername().equals("TestUser")) {
                deleteUser(u);
            }
        });

    }

    @Test
    void clientHandlerRunTest() {
        User mockMe = Mockito.mock(User.class);

        // Redirect System.out to capture the output
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));

        TestTCP serverTCP = new TestTCP();

        serverTCP.start();

        // Attendre un certain temps pour que le serveur puisse démarrer
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (Socket clientSocket = new Socket("localhost", 1556)) {
            System.out.println("CLIENT: Connected to server on port " + 1556);

            User testUser = new User("TestUser",clientSocket.getInetAddress(),true);
            DatabaseController.addUser(testUser);
            createChatTable(getUserID2(clientSocket.getInetAddress()));
            //ChatApp chatApp = new ChatApp(testUser,mockMe);

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println("Test Message\n");

            // Sleep to allow the client handler to process the message
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Restore the original System.out
            System.setOut(System.out);

            // Assert that the client handler processed the message
            assertTrue(capturedOutput.toString().contains("Received: Test Message"));
        } catch (IOException e) {
            System.out.println("CLIENT: Failed to connect to server");
        }
        finally {
            serverTCP.interrupt();
            deleteTestChatTable();
        }


    }
/*
    @Test
    void endConnectionTest() throws Exception {
        TestTCP serverTCP = new TestTCP();

        serverTCP.start();

        // Mock the static method of Socket class
        Socket fakeSocket = Mockito.mock(Socket.class);

        // Create a new client handler with the fake socket
        ServerTCP.ClientHandler clientHandler = new ServerTCP.ClientHandler(new Socket(InetAddress.getLoopbackAddress(),12345));
        PowerMockito.whenNew(ServerTCP.ClientHandler.class).withArguments(fakeSocket).thenReturn(clientHandler);

        Mockito.verifyNoMoreInteractions(fakeSocket);
        // End the connection
        (new ServerTCP.ClientHandler(fakeSocket)).endConnection();

        serverTCP.interrupt();
    }*/



    private void deleteTestChatTable() {
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

    public static class TestTCP extends Thread {
        public void run()
        {
            int port = 12345;
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server is listening on port " + port);
                System.out.println("SERVER: Waiting for a client connection");
                serverSocket.accept();
                System.out.println("SERVER: Client connection accepted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
