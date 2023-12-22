package Controller.Chat;
import java.io.*;
import java.net.*;

public class ServerTCP {
    public static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        public void run() {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (true) {
                    System.out.println("CONVERSATION: Waiting for message");
                    String inputLine = in.readLine();
                    //If end connection message received from client
                    if ("END".equals(inputLine)) {
                        out.println("END");
                        break;
                    }
                    System.out.println("Received: " + inputLine);
                }
                endConnection();
                System.out.println("CONVERSATION: Connection ended with client");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void endConnection() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 1556;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                System.out.println("SERVER: Waiting for a client connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("SERVER: Client connection accepted");
                // Create a new thread to handle a client
                Thread clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            }
        }

    }

}


