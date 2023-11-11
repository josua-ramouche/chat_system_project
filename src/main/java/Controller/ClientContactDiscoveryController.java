package Controller;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class ClientContactDiscoveryController {

    private static final String Username = "Lucile";

    public static void broadcast(String broadcastMessage, InetAddress address, DatagramSocket socket) throws IOException {
        byte[] buffer = broadcastMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
    }

    static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
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

    public static void sendEndConnection(InetAddress ip_address, int nport, DatagramSocket socket) {
        try {
            byte[] buf = "end".getBytes();
            DatagramPacket outPacket = new DatagramPacket(buf, buf.length, ip_address, nport);
            socket.send(outPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class EchoClient extends Thread {

        private final DatagramSocket socket;

        public EchoClient(DatagramSocket socket) {
            this.socket = socket;
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

                InetAddress address = packet.getAddress();
                String received = new String(packet.getData(), 0, packet.getLength());

                if (!received.equals("") && !received.equals("end")) {
                    System.out.println("Received address : " + address);
                    System.out.println(received);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        List<InetAddress> broadcastList = listAllBroadcastAddresses();

        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        for (InetAddress inetAddress : broadcastList) {
            try {
                System.out.println(inetAddress);
                broadcast(Username, inetAddress, socket);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        Thread Client = new EchoClient(socket);
        Client.start();

        // To demonstrate sending the "end" message
        // Uncomment the line below after testing the initial functionality
        // sendEndConnection(broadcastList.get(0), 4445, socket);
    }
}
