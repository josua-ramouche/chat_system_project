package Controller;

import Model.User;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import java.util.concurrent.TimeUnit;

public class ClientContactDiscoveryController {

    public static void broadcast(String broadcastMessage, InetAddress address) throws IOException{
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

    public static void sendUsername(List<InetAddress> broadcastList, User client) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String username = client.getUsername();

            // Vérifier l'unicité du nom d'utilisateur avant d'envoyer le premier broadcast
            if (isUsernameUnique(username, client.getContactList())) {
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
            // Gérer l'exception ici selon les besoins de votre application
        }
    }

    private static boolean isUsernameUnique(String username, List<User> contactList) {
        return contactList.stream()
                .noneMatch(u -> u.getUsername().equals(username));
    }



    public static void sendChangeUsername(User client, String newUsername) {
        // Utilisation d'un try-with-resources pour fermer automatiquement le socket
        try (DatagramSocket socket = new DatagramSocket()) {
            // Notify other users about the new username
            client.getContactList().forEach(u -> {
                try {
                    if (!u.getIPaddress().equals(client.getIPaddress())) {
                        broadcast("CHANGE_USERNAME:" + client.getUsername() + ":" + newUsername, u.getIPaddress());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // Update the client's username
            client.setUsername(newUsername);
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'exception ici selon les besoins de votre application
        }
    }





    public static void sendEndConnection(User client){
        System.out.println("Disconnection...");
        // To demonstrate sending the "end" message
        client.getContactList().forEach(u -> { try {
            broadcast("end",u.getIPaddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        });
        client.setState(false);
    }


}
