package Controller.Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatController {
    public static class listenTCP extends Thread {
        public void run()
        {
            int port = 1556;
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server is listening on port " + port);

                while (true) {
                    System.out.println("SERVER: Waiting for a client connection");
                    final Socket clientSocket = serverSocket.accept();
                    ClientTCP.getMap().put(clientSocket.getInetAddress(),clientSocket);
                    System.out.println("SERVER: Client connection accepted");
                    // Create a new thread to handle a client
                    Thread clientThread = new ServerTCP.ClientHandler(clientSocket);
                    clientThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
