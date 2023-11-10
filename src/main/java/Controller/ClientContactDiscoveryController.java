package Controller;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class ClientContactDiscoveryController {
        private static DatagramSocket socket;

        public static void broadcast(
                String broadcastMessage, InetAddress address) throws IOException {
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] buffer = broadcastMessage.getBytes();

            DatagramPacket packet
                    = new DatagramPacket(buffer, broadcastMessage.length(), address, 4445);
            socket.send(packet);
        }

        static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
            List<InetAddress> broadcastList = new ArrayList<>();
            Enumeration<NetworkInterface> interfaces
                    = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                networkInterface.getInterfaceAddresses().stream()
                        .map(InterfaceAddress::getBroadcast)
                        .filter(Objects::nonNull)
                        .forEach(broadcastList::add);
            }
            return broadcastList;
        }

        //prévenir les autres utilisateurs deconnexion
    public static void sendEndConnection(InetAddress ip_address, int nport) {

        try{
            byte[] buf;  // max size of the buffer : message length < buffer

            buf = "end".getBytes();
            DatagramPacket outPacket = new DatagramPacket(buf, buf.length, ip_address, nport); // Package content sent to senderAdress (senderPort)
            socket.send(outPacket); // those lines are sending the packet

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class EchoClient extends Thread {

        private final byte[] buf = new byte[256];

        public void run() {
            boolean running = true;

            while (running) {
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String received
                        = new String(packet.getData(), 0, packet.getLength());


                if (received.equals("response_ip")) {
                    System.out.println("Received address : " + address);
                }

                //   /!\
                //if user disconnected then send message end with function sendEndConnection to others users by broadcast
                //   /!\

            }
            socket.close();
        }
    }


    public static void main(String[] args) throws IOException {

        List<InetAddress> broadcastList = listAllBroadcastAddresses();

        for (InetAddress inetAddress : broadcastList) {
            try {
                System.out.println(inetAddress);
                broadcast("IP_address", inetAddress);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        Thread Client = new EchoClient();
        Client.start();

    }
}
