package Controller.Chat;

import Controller.Database.DatabaseController;
import Model.Message;
import Model.User;
import View.ChatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerTCP {

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


                    //ClientTCP.getMap().put(clientSocket.getInetAddress(),clientSocket);
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

    public static class ClientHandler extends Thread {
        private Socket clientSocket;

        private PrintWriter out;

        private BufferedReader in;

        private InetAddress ipReceived;

        public ClientHandler(Socket socket, InetAddress ip) throws IOException {
            clientSocket = socket;
            ipReceived=ip;
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }


        public synchronized void run() {
            try {
                System.out.println("THREAD NUMERO : ");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    //inputLine = in.readLine();
                    //if (inputLine!=null) {
                    System.out.println("Received: " + inputLine);

                    int idsender = DatabaseController.getUserID2(clientSocket.getInetAddress());
                    System.out.println("ID SENDER : " + idsender);
                    System.out.println("IP ADDRESS CLIENT: " + clientSocket.getInetAddress().getHostAddress());
                    System.out.println("CONTENT :" + inputLine);
                    DatabaseController.saveReceivedMessage(idsender, inputLine);

                    System.out.println("id sender from server TCP : " +idsender);


                    User partner = ChatApp.getPartner();
                    if (partner.getIPAddress().equals(clientSocket.getInetAddress())) {
                        ChatApp.PrintHistory(InetAddress.getByName(clientSocket.getInetAddress().getHostAddress()));
                    }

                   // }
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
