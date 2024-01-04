package Controller.Chat;

import Controller.Database.DatabaseController;
import Model.Message;
import View.ChatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerTCP {
    public static class ClientHandler extends Thread {
        private static Socket clientSocket = null;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
        }


        public void run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String inputLine;
                while (!clientSocket.isClosed()) {
                    inputLine = in.readLine();
                    if (inputLine!=null) {
                        System.out.println("Received: " + inputLine);

                        int idsender = DatabaseController.getUserID2(clientSocket.getInetAddress());
                        System.out.println("ID SENDER : " + idsender);
                        System.out.println("IP ADDRESS CLIENT: " + clientSocket.getInetAddress().getHostAddress());
                        System.out.println("CONTENT :" + inputLine);
                        DatabaseController.saveReceivedMessage(idsender, inputLine);

                        List<Message> messages = DatabaseController.getMessages(idsender);
                        ChatApp.PrintHistory(messages);

                        // If end connection message received from client
                        if ("END".equals(inputLine)) {
                            out.println("END");
                            break;
                        }
                    }
                }


                System.out.println("CONVERSATION: Connection ended with client");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void endConnection() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
