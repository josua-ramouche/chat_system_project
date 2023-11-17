package Controller;

import Model.User;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import java.util.concurrent.TimeUnit;

public class ClientContactDiscoveryController {
    private static final User client = new User();


    public ClientContactDiscoveryController() throws UnknownHostException { // ...(User u)
        //this.user = user;
        //FOR TESTING PURPOSES
        client.setUsername("Lucile");
        client.setIPaddress(InetAddress.getLocalHost());
        client.setState(true);

    }

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

    public static void sendEndConnection(DatagramSocket socket,Thread cli){
        System.out.println("Disconnection...");
        // To demonstrate sending the "end" message
        client.getContactList().forEach(u -> { try {
            broadcast("end",u.getIPaddress(),socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        });
        client.setState(false);
        cli.interrupt();
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class EchoClient extends Thread {

        private final DatagramSocket socket;

        public EchoClient(DatagramSocket socket) {
            this.socket = socket;
        }

        public void run() {

            while (client.getState()) {
                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                try {
                    socket.receive(packet);
                } catch (Exception e) {
                    this.interrupt();
                    //throw new RuntimeException(e);
                }

                InetAddress address = packet.getAddress();
                String received = new String(packet.getData(), 0, packet.getLength());

                if (!received.equals("") && !received.equals("end")) {
                    //New contact creation
                    User contact = new User();
                    contact.setUsername(received);
                    contact.setIPaddress(address);
                    contact.setState(true);
                    if(!client.containsContact(client.getContactList(),contact)) {
                        //Addition to contact list
                        client.addContact(contact);
                        System.out.println("New contact added");
                    }
                    client.getContactList().forEach(u -> System.out.println(u.getUsername()));
                    client.getContactList().forEach(u -> System.out.println(u.getIPaddress()));
                    client.getContactList().forEach(u -> System.out.println(u.getState()));
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        new ClientContactDiscoveryController();

        List<InetAddress> broadcastList = listAllBroadcastAddresses();

        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        for (InetAddress inetAddress : broadcastList) {
            try {
                System.out.println("Broadcast address : " + inetAddress);
                broadcast(client.getUsername(), inetAddress, socket);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        Thread Client = new EchoClient(socket);
        Client.start();

        //HOW TO DELAY BEFORE DISCONNECTION
        TimeUnit.SECONDS.sleep(3);
        sendEndConnection(socket,Client);
    }
}
