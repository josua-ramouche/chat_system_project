package Controller.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientTCP {
    private static Map<InetAddress, Socket> socketMap = new HashMap<>();
    private static PrintWriter out;
    private static BufferedReader in;

    public static Map<InetAddress, Socket> getMap() {
        return socketMap;
    }

    public static void startConnection(InetAddress ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            if (socketMap.containsKey(ip)) {
                out = new PrintWriter(socketMap.get(ip).getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socketMap.get(ip).getInputStream()));
                System.out.println("Welcome to the ChatSystem client already in\n");
            }
            else {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                socketMap.put(ip, socket); // Store the socket in the map
                System.out.println("Welcome to the ChatSystem client add to map\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        out.println(message);
    }

    public static void stopConnection() {
        try {
            in.close();
            out.close();
            for (Socket socket : socketMap.values()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
