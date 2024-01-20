package Controller.Chat;

import Controller.Database.DatabaseController;
import View.ChatApp;

import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerTCP {
    //Listening thread which creates a ClientHandler thread for each received TCP connection
    public static class listenTCP extends Thread {
        public void run()
        {
            int port = 1556;
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server is listening on port " + port);
                while (true) {
                    System.out.println("SERVER: Waiting for a client connection");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("SERVER: Client connection accepted");
                    // Create a new thread to handle a client
                    Thread clientThread = new ServerTCP.ClientHandler(clientSocket,clientSocket.getInetAddress());
                    clientThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //A ClientHandler is created for each chat (between us and a connected user)
    public static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private InetAddress ipReceived;

        //Constructor
        public ClientHandler(Socket socket, InetAddress ip) throws IOException {
            clientSocket = socket;
            ipReceived=ip;
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public synchronized void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    int idsender = DatabaseController.getUserID2(clientSocket.getInetAddress());
                    System.out.println("ID SENDER : " + idsender);
                    System.out.println("IP ADDRESS CLIENT: " + clientSocket.getInetAddress().getHostAddress());
                    System.out.println("CONTENT :" + inputLine);
                    DatabaseController.saveReceivedMessage(idsender, inputLine);
                    ChatApp.PrintHistory(idsender);
                    //}

                   // }
                    if(in == null) {
                        break;
                    }
                }
                System.out.println("CONVERSATION: Connection ended with client");
                //If PrintHistory is called with id -1, then the connection is ended, and we can not use the chat with this user anymore (if we were on the ChatApp)
                ChatApp.PrintHistory(-1);
            } catch (IOException | BadLocationException e) {
                ClientTCP.removeIP(ipReceived);
                e.printStackTrace();
            }
        }
    }
}
