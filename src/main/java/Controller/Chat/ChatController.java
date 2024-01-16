package Controller.Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatController {
    public static class listenTCP extends Thread {
        int i =0;
        public void run()
        {

            int port = 1556;
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server is listening on port " + port);

                while (true) {
                    i=i+1;
                    System.out.println("SERVER: Waiting for a client connection");
                    System.out.println("serversocket : " + serverSocket.getInetAddress().getHostAddress());

                    Socket clientSocket = serverSocket.accept();

                    System.out.println("clientSocket : " + clientSocket.getInetAddress().getHostAddress());

                    System.out.println("port clientSocket : " + clientSocket.getPort());


                    ClientTCP.getMap().put(clientSocket.getInetAddress(),clientSocket);
                    System.out.println("SERVER: Client connection accepted");
                    // Create a new thread to handle a client
                    Thread clientThread = new ServerTCP.ClientHandler(clientSocket,i);
                    clientThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
