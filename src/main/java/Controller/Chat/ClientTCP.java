package Controller.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientTCP {
    //private  Map<InetAddress, Socket> socketMap = new HashMap<>();
    private static PrintWriter out;
    private static BufferedReader in;

    private static List<Thread> clientList = new ArrayList<>();

    private static List<InetAddress> ipList = new ArrayList<>();

    public static void startConnection(InetAddress ip, int port) {
        try {
            if(!ipList.contains(ip) && !ServerTCP.listenTCP.getListIP().contains(ip)) {
                Socket socket = new Socket(ip, port);
                ipList.add(ip);
                ServerTCP.ClientHandler clientHandler = new ServerTCP.ClientHandler(socket, ip);
                clientList.add(clientHandler);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Welcome to the ChatSystem client\n");
                clientHandler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        out.println(message);
    }

    public static void removeIP(InetAddress ip) {
        ipList.remove(ip);
    }

    public static List<InetAddress> getIPList() {
        return ipList;
    }

    public static void stopConnection() throws IOException {
        System.out.println("Tentative de déconnexion");
        for (Thread clientHandler: clientList
             ) {
            clientHandler.interrupt();
        }
        in.close();
        out.close();
    }
}
