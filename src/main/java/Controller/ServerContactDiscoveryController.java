package Controller;

import Model.User;

import java.io.IOException;
import java.net.*;

import static Controller.ClientContactDiscoveryController.broadcast;

public class ServerContactDiscoveryController {

    public static class EchoServer extends Thread {
        private User server;
        private final DatagramSocket socket;

        public EchoServer(User server) throws SocketException {
            this.socket = new DatagramSocket(1556);
            this.server = server;
        }

        public EchoServer(User server, DatagramSocket sock) {
            this.socket = sock;
            this.server = server;
        }

        public User getServer() {
            return this.server;
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
                    e.printStackTrace();
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
                    } else if (received.startsWith("CHANGE_USERNAME:")) {
                        System.out.println("Change of username request:");
                        //recoit message changement username
                        handleChangeUsernameMessage(received, address);
                        System.out.println("-----------------------------");
                    } else if (received.startsWith("USERNAME_NOT_UNIQUE")) {
                        handleNotUnique(received.substring("USERNAME_NOT_UNIQUE:".length()-1), address);
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

        public void handleNotUnique(String message, InetAddress address) {
            System.out.println("Your new username is already used by someone, you cannot change your username.");
            System.out.println("Your username is: " + message);
        }

        public void handleBroadcastMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String username = parts[0];

            if (isUsernameUnique(username)) {
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
                server.getContactList().forEach(u -> {
                    if (u.getState()) {
                        System.out.println(u.getUsername());
                    }
                });

                sendIP(server.getUsername(), address, socket);
            } else {
                // Notify the client that the username is not unique
                sendIP("USERNAME_NOT_UNIQUE:", address, socket);
                System.out.println("Username '" + username + "' is not unique. Notifying the client.");
            }
        }



        private InetAddress lastResponseSender = null;

        public void handleResponseMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String username = parts[0];

            // Ne pas répondre au même expéditeur
            if (address.equals(lastResponseSender)) {
                return;
            }

            lastResponseSender = address;

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
            server.getContactList().forEach(u -> {
                if (u.getState()) {
                    System.out.println(u.getUsername());
                }
            });
        }



        public void handleEndMessage(InetAddress address) {
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

        public void handleChangeUsernameMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String oldUsername = parts[1];
            String newUsername = parts[2];

            // Vérifier l'unicité du nouveau nom d'utilisateur uniquement parmi les utilisateurs connectés
            if (isUsernameUnique(newUsername, address)) {
                for (User u : server.getContactList()) {
                    if (u.getUsername().equals(oldUsername)) {
                        u.setUsername(newUsername);
                        System.out.println("Username changed: " + oldUsername + " to " + newUsername);
                        break;
                    }
                }

                // Notify other users about the username change
                server.getContactList().forEach(u -> {
                    try {
                        if (!u.getIPaddress().equals(address)) {
                            broadcast("CHANGE_USERNAME:" + oldUsername + ":" + newUsername, u.getIPaddress());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Update the client's username
                server.getContactList().forEach(u -> {
                    if (u.getIPaddress().equals(address)) {
                        u.setUsername(newUsername);
                    }
                });

                System.out.println("Contact List (connected):");
                server.getContactList().forEach(u -> {
                    if (u.getState()) {
                        System.out.println(u.getUsername());
                    }
                });
            } else {
                // Notify the client that the new username is not unique
                sendIP("USERNAME_NOT_UNIQUE"+oldUsername, address, socket);
                System.out.println("Username '" + newUsername + "' is not unique. Notifying the client.");
            }
        }

        public boolean isUsernameUnique(String username, InetAddress requesterAddress) {
            if (!username.equals(server.getUsername())) {
                return server.getContactList().stream()
                        .filter(u -> u.getState() && !u.getIPaddress().equals(requesterAddress)) // Filtrer uniquement les utilisateurs connectés, excluant le demandeur
                        .noneMatch(u -> u.getUsername().equals(username));
            }
            else return false;
        }
        public boolean isUsernameUnique(String username) {
            if (!username.equals(server.getUsername())) {
                return server.getContactList().stream()
                        .filter(u -> u.getState()) // Filtrer uniquement les utilisateurs connectés
                        .noneMatch(u -> u.getUsername().equals(username));
            }
            else return false;
        }



    }
}
