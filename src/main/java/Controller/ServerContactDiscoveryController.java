package Controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerContactDiscoveryController {

    private static final String Username = "Josua";

    public static class EchoServer extends Thread {

        private final DatagramSocket socket;

        public EchoServer(DatagramSocket socket) {
            this.socket = socket;
        }

        public static void sendIP(String message, InetAddress ip_address, int nport, DatagramSocket socket) {
            try {
                byte[] buf = message.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, ip_address, nport);
                socket.send(outPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            boolean running = true;

            while (running) {
                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Received");

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String received = new String(packet.getData(), 0, packet.getLength());

                System.out.println("address :" + address);
                System.out.println("port : " + port);
                System.out.println("Received : " + received);

                if (!received.equals("") && !received.equals("end")) {
                    System.out.println("check");
                    sendIP(Username, address, port, socket);
                    System.out.println(received);
                }

                //retirer la personne des gens connectés
                if (received.equals("end")) {
                    running = false;
                }
            }
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(4445);
        Thread Server = new EchoServer(serverSocket);
        Server.start();
    }
}
