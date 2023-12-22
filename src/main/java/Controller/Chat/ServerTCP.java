package Controller.Chat;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class ServerTCP {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    public void start(int port) {
        try{
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Welcome to the ChatSystem client\n");

            while (true) {
                String inputLine = in.readLine();
                if (".".equals(inputLine)) {
                    out.println("END");
                    stop();
                    System.out.println("Connection ended");
                    break;
                }
                System.out.println("Received: " + inputLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try{
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ServerTCP server=new ServerTCP();
        server.start(1789);
    }
}
