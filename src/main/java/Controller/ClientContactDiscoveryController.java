package Controller;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class ClientContactDiscoveryController {
    public static void main(String[] args) throws IOException {

            List<InetAddress> broadcastList = listAllBroadcastAddresses();

        for (InetAddress inetAddress : broadcastList) {
            try {
                System.out.println(inetAddress);
                broadcast("Hello", inetAddress);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        }

        public static void broadcast(
                String broadcastMessage, InetAddress address) throws IOException {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] buffer = broadcastMessage.getBytes();

            DatagramPacket packet
                    = new DatagramPacket(buffer, buffer.length, address, 4445);
            socket.send(packet);
            socket.close();
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
}
