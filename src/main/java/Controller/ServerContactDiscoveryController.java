package Controller;
import Model.User;
import View.CustomListener;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import static Controller.ClientContactDiscoveryController.broadcast;

public class ServerContactDiscoveryController {
    public static class EchoServer extends Thread {
        private final User server;
        private final DatagramSocket socket;
        private List<InetAddress> interfacesIP;

        // Constructor
        public EchoServer(User server, List<InetAddress> interfacesIP) throws SocketException {
            this.socket = new DatagramSocket(1556);
            this.server = server;
            this.interfacesIP = ClientContactDiscoveryController.getInterfacesIP();
            //this.interfacesIP = interfacesIP;
        }

        // For testing usage
        public EchoServer(User server, DatagramSocket sock) {
            this.socket = sock;
            this.server = server;
        }

        public User getServer() {
            return this.server;
        }

        // Send a message to a specific IP address
        public static void sendIP(String message, InetAddress ip_address, DatagramSocket socket) {
            try {
                byte[] buf = message.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, ip_address, 1556);
                socket.send(outPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Thread run
        public void run() {
            System.out.println("-----------------------------");
            // Stops if the user is disconnected (if getState() is false)
            while (server.getState()) {
                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InetAddress address = packet.getAddress();
                String received = new String(packet.getData(), 0, packet.getLength());

                // Allows to handle all the different received messages
                if (!received.equals("") && !address.equals(server.getIPAddress()) && !interfacesIP.contains(address)) {
                    if (received.startsWith("BROADCAST:")) {
                        // if a broadcast message is received
                        System.out.println("Broadcast:");
                        handleBroadcastMessage(received.substring("BROADCAST:".length()), address);
                        System.out.println("-----------------------------");
                    } else if (received.equals("end")) {
                        // if an end connection message is received
                        System.out.println("End of connection received:");
                        handleEndMessage(address);
                        System.out.println("-----------------------------");
                    } else if (received.startsWith("CHANGE_USERNAME:")) {
                        // if a user asks for a change of username
                        System.out.println("Change of username request:");
                        handleChangeUsernameMessage(received, address);
                        System.out.println("-----------------------------");
                    } else if (received.startsWith("USERNAME_NOT_UNIQUE")) {
                        // if the permission to change the username is declined
                        handleNotUnique(received.substring("USERNAME_NOT_UNIQUE:".length()));
                    } else if (received.startsWith("USERNAME_UPDATED")) {
                        // if the permission to change the username is accepted
                        handleChangeOfUsername(received.substring("USERNAME_UPDATED:".length()));
                    } else {
                        // if a response to a broadcast message is received
                        System.out.println("Broadcast response:");
                        handleResponseMessage(received, address);
                        System.out.println("-----------------------------");
                    }
                }
            }
            socket.close();
        }
        private InetAddress lastResponseSender = null;

        // Change of username accepted
        public void handleChangeOfUsername(String message) {
            System.out.println("You have changed your username");
            System.out.println("Your username is now: " + message);
        }

        //signals and slots for unicity with GUI
        private List<CustomListener> listeners = new ArrayList<>();

        public void addActionListener(CustomListener listener) {
            listeners.add(listener);
        }


        // Change of username declined
        public void handleNotUnique(String message) {
            String[] parts = message.split(":");
            String username = parts[0];
            if (!message.equals("")) {
                // the user do not change his username and will use the old one
                System.out.println("Your new username is already used by someone, you cannot change your username.");
                System.out.println("Your username is: " + username);
                for (CustomListener listener : listeners) {
                    listener.showPopup("Username not unique");
                }
            }
            else {
                // the user cannot connect to the application because his first username is not unique, he needs to change it first
                System.out.println("Your new username is already used by someone, try to enter a new username.");

            }
        }

        // Reception of a broadcast message
        public void handleBroadcastMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String username = parts[0];

            // checks if the user who sends the broadcast message has a unique username
            if (isUsernameUnique(username)) {
                User contact = new User();
                contact.setUsername(username);
                contact.setIPAddress(address);
                contact.setState(true);

                // if the sender is not already in the contact list, he is added to the contact list
                if (!server.containsContact(server.getContactList(), contact)) {
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

        // Reception of the response to the broadcast message
        public void handleResponseMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String username = parts[0];

            if (address.equals(lastResponseSender)) {
                return;
            }

            lastResponseSender = address;

            User contact = new User();
            contact.setUsername(username);
            contact.setIPAddress(address);
            contact.setState(true);

            // if the receiver is not already in the contact list, he is added to the contact list
            if (!server.containsContact(server.getContactList(), contact)) {
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

        // Reception of an end message
        public void handleEndMessage(InetAddress address) {
            String disconnectedUser = null;
            for (User u : server.getContactList()) {
                if (u.getIPAddress().equals(address)) {
                    // set the sender state to disconnected (false)
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

        // Ask for permission to change the sender's username
        public void handleChangeUsernameMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String oldUsername = parts[1];
            String newUsername = parts[2];

            //check if the sender new username is unique
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
                        if (!u.getIPAddress().equals(address)) {
                            broadcast("CHANGE_USERNAME:" + oldUsername + ":" + newUsername, u.getIPAddress());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Update the client's username
                server.getContactList().forEach(u -> {
                    if (u.getIPAddress().equals(address)) {
                        u.setUsername(newUsername);
                    }
                });

                System.out.println("Contact List (connected):");
                server.getContactList().forEach(u -> {
                    if (u.getState()) {
                        System.out.println(u.getUsername());
                    }
                });
                // Send a message to the sender to inform him that his username can be changed
                sendIP("USERNAME_UPDATED"+newUsername, address, socket);
            } else {
                // Notify the client that the new username is not unique
                sendIP("USERNAME_NOT_UNIQUE"+oldUsername, address, socket);
                System.out.println("Username '" + newUsername + "' is not unique. Notifying the client.");
            }
        }

        // Check among the connected users in the contact list that a username is unique (except himself)
        public boolean isUsernameUnique(String username, InetAddress requesterAddress) {
            if (!username.equals(server.getUsername())) {
                return server.getContactList().stream()
                        .filter(u -> u.getState() && !u.getIPAddress().equals(requesterAddress))
                        .noneMatch(u -> u.getUsername().equals(username));
            }
            else return false;
        }

        // Check among the connected users in the contact list that a username is unique (except himself)
        public boolean isUsernameUnique(String username) {
            if (!username.equals(server.getUsername())) {
                return server.getContactList().stream()
                        .filter(User::getState)
                        .noneMatch(u -> u.getUsername().equals(username));
            }
            else return false;
        }
    }
}
