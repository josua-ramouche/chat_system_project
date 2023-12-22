package Controller.Chat;

import java.io.*;
import java.net.*;

public class ClientTCP {
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static String startConnection(String ip, int port) {
        String resp = "";
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            resp = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public static void sendMessage() throws IOException {
        // System.in is an InputStream which we wrap into the more capable BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();
        out.println(input);
    }

    public static void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(startConnection("127.0.0.1",1789));
        while (true) {
            sendMessage();
        }
    }

}
