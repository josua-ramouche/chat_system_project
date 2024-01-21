package Controller.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientTCP {
    private static PrintWriter out;
    private static BufferedReader in;
    private static List<Thread> clientList = new ArrayList<>();
    private static List<InetAddress> ipList = new ArrayList<>();

    //Initiate a TCP connection between two users
    public static void startConnection(InetAddress ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            if(!ipList.contains(ip)) {
                ipList.add(ip);
                ServerTCP.ClientHandler clientHandler = new ServerTCP.ClientHandler(socket, ip);
                clientList.add(clientHandler);
                System.out.println("Welcome to the ChatSystem client\n");
                clientHandler.start();
            }
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Out getter
    public static PrintWriter getOut() {
        return out;
    }

    //In getter
    public static BufferedReader getIn() {
        return in;
    }

    //Send a TCP message
    public static void sendMessage(String message) {
        out.println(message);
    }

    //Send end TCP
    public static void endTCP() {
        out.println("endTCP");
    }

    //Remove the IP address from the ipList
    public static void removeIP(InetAddress ip) {
        ipList.remove(ip);
    }

    //ipList getter
    public static List<InetAddress> getIPList() {
        return ipList;
    }

    //ipList setter
    public static void setIPList(List<InetAddress> listIP) {
        ipList = listIP;
    }
}
