package Controller;

import Model.User;

import java.io.IOException;
import java.net.*;

public class ServerContactDiscoveryController {

    public static class EchoServer extends Thread {
        private User server;
        private final DatagramSocket socket;

        public EchoServer(User server) throws SocketException {
            this.socket = new DatagramSocket(1556);
            this.server = server;
        }

        public static void sendIP(String message, InetAddress ip_address, DatagramSocket socket) {
            try {
                byte[] buf = message.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, ip_address, 1556);
                socket.send(outPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            System.out.println("-----------------------------");
            while (server.getState()) {
                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                InetAddress address = packet.getAddress();

                String received = new String(packet.getData(), 0, packet.getLength());

                // Nouvelle condition pour distinguer entre la diffusion et la réponse
                if (!received.equals("") && !address.equals(server.getIPaddress())) {
                    if (received.startsWith("BROADCAST:")) {
                        // broadcast (recoit uniquement le username en enlevant BROADCAST: de la reception
                        System.out.println("Broadcast:");
                        handleBroadcastMessage(received.substring("BROADCAST:".length()), address);
                        System.out.println("-----------------------------");
                    } else if (received.equals("end")) {
                        // recoit message de deconnection d'un user
                        System.out.println("End of connection received:");
                        handleEndMessage(address);
                        System.out.println("-----------------------------");
                    } else {
                        // recoit reponse au broadcast
                        System.out.println("Broadcast response:");
                        handleResponseMessage(received, address);
                        System.out.println("-----------------------------");
                    }
                }
            }
            socket.close();
        }

        private void handleBroadcastMessage(String username, InetAddress address) {
            User contact = new User();
            contact.setUsername(username);
            contact.setIPaddress(address);
            contact.setState(true);

            if (!server.containsContact(server.getContactList(), contact)) {
                // Addition to contact list
                server.addContact(contact);
                System.out.println("New contact added");
            }
            System.out.println("Contact List (connected):");
            server.getContactList().forEach(u -> { if (u.getState()) { System.out.println(u.getUsername()); } });

            sendIP(server.getUsername(), address, socket);
        }

        private void handleResponseMessage(String username, InetAddress address) {
            User contact = new User();
            contact.setUsername(username);
            contact.setIPaddress(address);
            contact.setState(true);

            if (!server.containsContact(server.getContactList(), contact)) {
                // Addition to contact list
                server.addContact(contact);
                System.out.println("New contact added");
            }

            System.out.println("Contact List (connected):");
            server.getContactList().forEach(u -> { if (u.getState()) { System.out.println(u.getUsername()); } });
        }

        private void handleEndMessage(InetAddress address) {
            String disconnectedUser = null;
            for (User u : server.getContactList()) {
                if (u.getIPaddress().equals(address)) {
                    u.setState(false);
                    disconnectedUser = u.getUsername();
                    break;
                }
            }
            if (disconnectedUser != null) {
                System.out.println("User " + disconnectedUser + " disconnected");
            }
            System.out.println("Contact List (connected):");
            server.getContactList().forEach(u -> { if (u.getState()) { System.out.println(u.getUsername()); } });
        }
    }
}
