package Controller.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTCP {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void startConnection(InetAddress ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Welcome to the ChatSystem client\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        try {
            // System.in is an InputStream which we wrap into the more capable BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                out.println(message);
                // Sending end connection message to the server
                //if (message.equals("END")) {
                //    break;
                //}
        } finally {
            stopConnection();
        }
    }

    public static void stopConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
