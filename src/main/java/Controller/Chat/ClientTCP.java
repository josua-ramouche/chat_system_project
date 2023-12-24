package Controller.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTCP {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void startConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Welcome to the ChatSystem client\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage() {
        try {
            // System.in is an InputStream which we wrap into the more capable BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = reader.readLine()) != null) {
                out.println(input);
                // Sending end connection message to the server
                if (input.equals("END")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void main(String[] args) {
        startConnection("127.0.0.1", 1556);
        sendMessage();
    }
}
