package Controller.Chat;

import Controller.Database.DatabaseController;
import Model.Message;
import View.ChatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ServerTCP {
    public static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
        }


        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

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

                        System.out.println("id sender from server TCP : " +idsender);

                        ChatApp.PrintHistory(InetAddress.getByName(clientSocket.getInetAddress().getHostAddress()));


                    }
                }


                System.out.println("CONVERSATION: Connection ended with client");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*public static void endConnection() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

    }
}
