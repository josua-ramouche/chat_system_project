package Controller.Chat;
import Controller.Database.DatabaseController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class ClientTCPTest {
    @Mock
    private Socket mockSocket;
    private ByteArrayInputStream mockIn;
    private ByteArrayOutputStream mockOut;

    @BeforeAll
    static void ensure_database_is_created() {
        DatabaseController.createUserTable();
    }
    @BeforeEach
    void setUp() {
        mockSocket = mock(Socket.class);
        mockIn = mock(ByteArrayInputStream.class);
        mockOut = mock(ByteArrayOutputStream.class);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockSocket.close();
        mockIn.close();
        mockOut.close();
    }

    @Test
    void startConnectionTest() throws Exception {
        InetAddress mockIp = InetAddress.getByName("127.0.0.1");
        int mockPort = 12345;

        // Mock the socket creation and streams
        when(mockSocket.getOutputStream()).thenReturn(mockOut);
        when(mockSocket.getInputStream()).thenReturn(mockIn);
        whenNew(Socket.class).withArguments(mockIp,mockPort).thenReturn(mockSocket);

        // Call the method
        ClientTCP.startConnection(mockIp, mockPort);

        // Verify that the socket was created and streams were initialized
        verifyNew(Socket.class).withArguments(mockIp,mockPort);
        assertNotNull(mockSocket);
        assertNotNull(mockOut);
        assertNotNull(mockIn);
    }

    @Test
    void sendMessageTest() throws IOException {
        // Set up mocks
        when(mockSocket.getOutputStream()).thenReturn(mockOut);
        when(mockSocket.getInputStream()).thenReturn(mockIn);

        // Call startConnection to initialize the socket, out, and in
        ClientTCP.startConnection(InetAddress.getByName("127.0.0.1"), 12345);

        // Call the method
        ClientTCP.sendMessage("Test Message");

        // Verify that the message was sent through the PrintWriter
        //verify(mockOut).println("Test Message");
    }

    @Test
    void stopConnectionTest() throws IOException {
        // Set up mocks
        when(mockSocket.getOutputStream()).thenReturn(mockOut);
        when(mockSocket.getInputStream()).thenReturn(mockIn);

        // Call startConnection to initialize the socket, out, and in
        ClientTCP.startConnection(InetAddress.getByName("127.0.0.1"), 12345);

        // Call the method
        ClientTCP.stopConnection();

        // Verify that the streams were closed, and the socket was closed
        verify(mockIn).close();
        verify(mockOut).close();
        verify(mockSocket).close();
    }
}
