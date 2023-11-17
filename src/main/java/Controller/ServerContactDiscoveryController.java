package Controller;

import Model.User;

import java.io.IOException;
import java.net.*;

public class ServerContactDiscoveryController {

    public static class EchoServer extends Thread {
        private User server;
        private final DatagramSocket socket;

        public EchoServer(int port, User server) throws SocketException {
            this.socket = new DatagramSocket(port);
            this.server = server;
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
            System.out.println("SERVER");
            while (server.getState()) {
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


                //New contact creation
                User contact = new User();
                contact.setUsername(received);
                contact.setIPaddress(address);
                contact.setState(true);

                if (!received.equals("") && !received.equals("end")) {
                    if(!server.containsContact(server.getContactList(),contact)){
                        //Addition to contact list
                        server.addContact(contact);
                        System.out.println("New contact added");
                    }
                    server.getContactList().forEach(u -> System.out.println(u.getUsername()));
                    server.getContactList().forEach(u -> System.out.println(u.getIPaddress()));
                    server.getContactList().forEach(u -> System.out.println(u.getState()));
                    sendIP(server.getUsername(), address, port, socket);
                    System.out.println(received);
                }

                //Retirer la personne des gens connectés
                if (received.equals("end")) {
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
                }
            }
            socket.close();
        }
    }
/*
    public static void main(String[] args) throws IOException {
        ServerContactDiscoveryController serverConstruct = new ServerContactDiscoveryController();
        DatagramSocket serverSocket = new DatagramSocket(4445);
        Thread Server = new EchoServer(serverSocket);
        Server.start();
    }
    */

}
