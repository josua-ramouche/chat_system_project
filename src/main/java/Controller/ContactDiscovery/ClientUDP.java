package Controller.ContactDiscovery;
import Model.ContactList;
import Model.User;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class ClientUDP {
    // Send a broadcast message on the current network of the interface on socket 1556
    public static void broadcast(String broadcastMessage, InetAddress address) throws IOException{
        // Create a new datagram that is closed after the packet is sent
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] buffer = broadcastMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 1556);
            socket.send(packet);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
//
    // Create a list of all the broadcast addresses available on a computer
    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
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

    // Allow a user to use the application with multiple interfaces and IP addresses
    public static List<InetAddress> getInterfacesIP() throws SocketException {
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        List<InetAddress> interfacesIP = new ArrayList<>();
        while (e.hasMoreElements()) {
            NetworkInterface n = e.nextElement();
            Enumeration<InetAddress> ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = ee.nextElement();
                interfacesIP.add(i);
            }
        }
        return interfacesIP;
    }

    // Send the user's username with broadcast to other users in the network, a check is made to verify that the username is unique
    public static void sendUsername(List<InetAddress> broadcastList, User client) {
        try (DatagramSocket ignored = new DatagramSocket()) {
            String username = client.getUsername();
            if (isUsernameUnique(username, ContactList.getContacts())) {
                for (InetAddress inetAddress : broadcastList) {
                    try {
                        System.out.println("Broadcast address : " + inetAddress);
                        broadcast("BROADCAST:" + username, inetAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            } else {
                System.out.println("Username '" + username + "' is not unique. Please choose a different username.");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    // Check if a username is already present in the contact list of the user (other checks are made in the ServerUDP to check if the username is
    // present in other users' contact lists)
    public static boolean isUsernameUnique(String username, List<User> contactList) {
            return contactList.stream()
                    .noneMatch(u -> u.getUsername().equals(username));
    }

    // Send a broadcast message to ask for a change of username (checks if the username is unique on the server side to accept the demand or not)
    public static void sendChangeUsername(User client, String newUsername) {
        try (DatagramSocket ignored = new DatagramSocket()) {
            // Notify other users about the new username
            ContactList.getContacts().forEach(u -> {
                try {
                    if (!u.getIPAddress().equals(client.getIPAddress())) {
                        broadcast("CHANGE_USERNAME:" + client.getUsername() + ":" + newUsername, u.getIPAddress() );
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            // Update the client's username
            client.setUsername(newUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send a message in broadcast so other users can change his status to disconnected (false)
    public static void sendEndConnection(User client){
        System.out.println("Disconnection...");
        ContactList.getContacts().forEach(u -> { try {
            broadcast("end",u.getIPAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        });
        client.setState(false);
        System.out.println("You are now disconnected\n");
    }


}
