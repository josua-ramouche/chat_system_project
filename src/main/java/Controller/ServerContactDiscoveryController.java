package Controller;
import Model.User;
import java.io.IOException;
import java.net.*;
import java.util.List;
import static Controller.ClientContactDiscoveryController.broadcast;

public class ServerContactDiscoveryController {
    public static class EchoServer extends Thread {
        private final User server;
        private final DatagramSocket socket;
        private List<InetAddress> interfacesIP;

        //constructor
        public EchoServer(User server, List<InetAddress> interfacesIP) throws SocketException {
            this.socket = new DatagramSocket(1556);
            this.server = server;
            this.interfacesIP = interfacesIP;
        }

        //for testing usage
        public EchoServer(User server, DatagramSocket sock) {
            this.socket = sock;
            this.server = server;
        }

        public User getServer() {
            return this.server;
        }

        //send a message to a specific IP address
        public static void sendIP(String message, InetAddress ip_address, DatagramSocket socket) {
            try {
                byte[] buf = message.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, ip_address, 1556);
                socket.send(outPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Thread run
        public void run() {
            System.out.println("-----------------------------");
            //stops if the user is disconnected (if getState() is false)
            while (server.getState()) {
                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InetAddress address = packet.getAddress();
                String received = new String(packet.getData(), 0, packet.getLength());

                //Allows to handle all the different received messages
                if (!received.equals("") && !address.equals(server.getIPaddress()) && !interfacesIP.contains(address)) {
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
                        // if a user ask for a change of username
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

        // change of username accepted
        public void handleChangeOfUsername(String message) {
            System.out.println("You have changed your username");
            System.out.println("Your username is now: " + message);
        }

        // change of username declined
        public void handleNotUnique(String message) {
            String[] parts = message.split(":");
            String username = parts[0];
            if (!message.equals("")) {
                //the user do not change his username and will use the old one
                System.out.println("Your new username is already used by someone, you cannot change your username.");
                System.out.println("Your username is: " + username);
            }
            else {
                //the user can not connect to the application because his first username is not unique, he needs to change it first
                System.out.println("Your new username is already used by someone, try to enter a new username.");
            }
        }

        //reception of a broadcast message
        public void handleBroadcastMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String username = parts[0];

            //checks if the user who sends the broadcast message has a unique username
            if (isUsernameUnique(username)) {
                User contact = new User();
                contact.setUsername(username);
                contact.setIPaddress(address);
                contact.setState(true);

                //if the sender is not already in the contact list, he is added to the contact list
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

        //reception of the response of the broadcast message
        public void handleResponseMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String username = parts[0];

            if (address.equals(lastResponseSender)) {
                return;
            }

            lastResponseSender = address;

            User contact = new User();
            contact.setUsername(username);
            contact.setIPaddress(address);
            contact.setState(true);

            //if the receiver is not already in the contact list, he is added to the contact list
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

        //reception of end message
        public void handleEndMessage(InetAddress address) {
            String disconnectedUser = null;
            for (User u : server.getContactList()) {
                if (u.getIPaddress().equals(address)) {
                    //set the sender state to disconnected (false)
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

        //ask for permission to change the sender username
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
                //send a message to the sender to informs him that his username can be changed
                sendIP("USERNAME_UPDATED"+newUsername, address, socket);
            } else {
                // Notify the client that the new username is not unique
                sendIP("USERNAME_NOT_UNIQUE"+oldUsername, address, socket);
                System.out.println("Username '" + newUsername + "' is not unique. Notifying the client.");
            }
        }

        //check in the connected users in the contact list has a unique username (except himself)
        public boolean isUsernameUnique(String username, InetAddress requesterAddress) {
            if (!username.equals(server.getUsername())) {
                return server.getContactList().stream()
                        .filter(u -> u.getState() && !u.getIPaddress().equals(requesterAddress))
                        .noneMatch(u -> u.getUsername().equals(username));
            }
            else return false;
        }

        //check in the connected users in the contact list has a unique username
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
