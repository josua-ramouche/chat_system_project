package Controller.ContactDiscovery;
import Controller.Database.DatabaseController;
import Model.User;
import View.CustomListener;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import static Controller.ContactDiscovery.ClientUDP.broadcast;
import static Controller.Database.DatabaseController.getUserID;
import static Controller.Database.DatabaseController.printContactList;


public class ServerUDP {
//TEST

    public static class EchoServer extends Thread {
        private final User server;
        private final DatagramSocket socket;
        private List<InetAddress> interfacesIP;

        // Constructor
        public EchoServer(User server) throws SocketException {
            this.socket = new DatagramSocket(1556);
            this.server = server;
            this.interfacesIP = ClientUDP.getInterfacesIP();
        }

        // For testing usage
        public EchoServer(User server, DatagramSocket sock) {
            this.socket = sock;
            this.server = server;
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
                    } else if (received.startsWith("HANDLE_RESPONSE_MESSAGE")) {
                        // if a response to a broadcast message is received
                        handleResponseMessage(received.substring("HANDLE_RESPONSE_MESSAGE:".length()), address);
                        System.out.println("-----------------------------");
                    } else if (received.startsWith("HANDLE_RESPONSE_END")) {
                        System.out.println("End message received by distant server");
                        handleResponseEnd();
                    }
                }
            }
            //
            socket.close();
            System.out.println("Socket correctly closed");
        }
        private InetAddress lastResponseSender = null;

        // Change of username accepted
        public void handleChangeOfUsername(String message) {

            System.out.println("You have changed your username");
            System.out.println("Your username is now: " + message);
            for (CustomListener listener : listeners) {
                System.out.println("check launchtest");
                listener.launchTest();
                System.out.println("check launchtest");
            }
        }

        //signals and slots for unicity with GUI
        private final List<CustomListener> listeners = new ArrayList<>();

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
                    listener.notUniquePopup("Username not unique");
                }
            }
            else {
                for (CustomListener listener : listeners) {
                    listener.notUniquePopup("Username not unique");
                }
                // the user cannot connect to the application because his first username is not unique, he needs to change it first
                System.out.println("Your new username is already used by someone, try to enter a new username.");

            }
        }

        // Reception of a broadcast message
        public void handleBroadcastMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String username = parts[0];

            System.out.println("handle broadcast message : ");


            // checks if the user who sends the broadcast message has a unique username
            if (isUsernameUnique(username)) {
                User contact = new User();
                contact.setUsername(username);
                contact.setIPAddress(address);
                contact.setState(true);

                List<User> Users = DatabaseController.getUsers();


                // if the sender is not already in the contact list, he is added to the contact list
                if (!server.containsContact(Users, contact)) {
                    System.out.println("estce quon passe ici 1?");
                    System.out.println("IPADDRESS : " + address.getHostAddress());
                    //ContactList.addContact(contact); contact list retirée
                    //Adds user to the database
                    if(DatabaseController.containsUser(contact)) {
                        System.out.println("User already in database, updating username in database");
                        DatabaseController.updateUsername(contact, contact.getUsername());
                        DatabaseController.updateConnectionState(contact,true);
                    }
                    else {
                        System.out.println("User not registered in database, adding user to database");
                        DatabaseController.addUser(contact);
                        DatabaseController.createChatTable(getUserID(contact));
                    }
                    System.out.println("New contact added");
                }

                System.out.println("Contact List (connected):");
                printContactList();

                sendIP("HANDLE_RESPONSE_MESSAGE:"+server.getUsername(), address, socket);
                System.out.println("Socket : "+socket.getPort());

                //new listener vers interface contact list pour update
                for (CustomListener listener : listeners) {
                    System.out.println("check launchtest");
                    listener.launchTest();
                    System.out.println("check launchtest");
                }
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

            System.out.println("HANDLE RESPONSE MESSAGE!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            if (address.equals(lastResponseSender)) {
                return;
            }

            lastResponseSender = address;

            User contact = new User();
            contact.setUsername(username);
            contact.setIPAddress(address);
            contact.setState(true);

            List<User> Users = DatabaseController.getUsers();

            if (!server.containsContact(Users, contact)) {
                System.out.println("estce quon passe ici 2 ?");
                System.out.println("IPADDRESS : " + address.getHostAddress());
                //ContactList.addContact(contact); retirée contact list
                if(DatabaseController.containsUser(contact) && isUsernameUnique(contact.getUsername())) {
                    System.out.println("User already in database, updating username in database");
                    DatabaseController.updateUsername(contact, contact.getUsername());
                    DatabaseController.updateConnectionState(contact,true);
                }
                else {
                    System.out.println("User not registered in database, adding user to database");
                    DatabaseController.addUser(contact);
                    DatabaseController.createChatTable(getUserID(contact));
                }
                System.out.println("New contact added");
            }

            System.out.println("Contact List (connected):");
            printContactList();
            for (CustomListener listener : listeners) {
                System.out.println("check launchtest");
                listener.launchTest();
                System.out.println("check launchtest");
            }
        }

        // Reception of an end message
        public void handleEndMessage(InetAddress address) {
            String disconnectedUser = null;
            System.out.println("ON EST DANS HANDLEENDMESSAGE");
            for (User u : DatabaseController.getUsers()) {
                if (u.getIPAddress().equals(address)) {
                    // set the sender state to disconnected (false)
                    System.out.println("Update de l'état de connection dans la database en cours...");
                    DatabaseController.updateConnectionState(u,false);
                    System.out.println("Utilisateur + " + u.getUsername() + " déconnecté dans la database");
                    u.setState(false);
                    System.out.println("Etat de l'utilisateur à false");
                    disconnectedUser = u.getUsername();
                    System.out.println("Disconnected User : " + disconnectedUser);
                    break;
                }
            }
            if (disconnectedUser != null) {
                System.out.println("User " + disconnectedUser + " disconnected");
            }
            System.out.println("Contact List (connected) after disconnection:");
            //ContactList.getContacts().forEach(u -> { if (u.getState()) { System.out.println(u.getUsername()); } });
            for (CustomListener listener : listeners) {
                System.out.println("check launchtest");
                listener.launchTest();
                System.out.println("check launchtest");
            }
            sendIP("HANDLE_RESPONSE_END", address, socket);
        }

        public void handleResponseEnd() {
            server.setState(false);
            System.out.println("You are now disconnected\n");
        }

        // Ask for permission to change the sender's username
        public void handleChangeUsernameMessage(String message, InetAddress address) {
            String[] parts = message.split(":");
            String oldUsername = parts[1];
            String newUsername = parts[2];
            List<User> Users = DatabaseController.getUsers();
            System.out.println("ask for change of username received");
            //check if the sender new username is unique
            if (isUsernameUnique(newUsername, address)) {
                for (User u : Users) {
                    if (u.getUsername().equals(oldUsername)) {
                        //Updates username in database
                        DatabaseController.updateUsername(u,newUsername);
                        u.setUsername(newUsername);
                        System.out.println("Username changed: " + oldUsername + " to " + newUsername + "in the database");
                        break;
                    }
                }


                // Send a message to the sender to inform him that his username can be changed
                sendIP("USERNAME_UPDATED"+newUsername, address, socket);

            } else {
                // Notify the client that the new username is not unique
                sendIP("USERNAME_NOT_UNIQUE"+oldUsername, address, socket);
                System.out.println("Username '" + newUsername + "' is not unique. Notifying the client.");
            }
            for (CustomListener listener : listeners) {
                System.out.println("check launchtest");
                listener.launchTest();
                System.out.println("check launchtest");
            }
        }

        // Check among the connected users in the contact list that a username is unique (except himself)
        public boolean isUsernameUnique(String username, InetAddress requesterAddress) {
            List<User> Users = DatabaseController.getAllUsers();
            if (!username.equals(server.getUsername())) {
                return Users.stream()
                        .filter(u -> !u.getIPAddress().equals(requesterAddress))
                        .noneMatch(u -> u.getUsername().equals(username));
            }
            else return false;
        }

        // Check among the connected users in the contact list that a username is unique (except himself)
        public boolean isUsernameUnique(String username) {
            List<User> Users = DatabaseController.getAllUsers();
            if (!username.equals(server.getUsername())) {
                return Users.stream()
                        .noneMatch(u -> u.getUsername().equals(username));
            }
            else return false;
        }
    }
}