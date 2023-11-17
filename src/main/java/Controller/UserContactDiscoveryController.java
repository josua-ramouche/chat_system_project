package Controller;

import Model.User;

import java.io.IOException;
import java.util.List;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static Controller.ClientContactDiscoveryController.*;

public class UserContactDiscoveryController {

    //Client actions (send broadcast for contact discovery, change of username, end connection)
    //Add server users to contact list

    private static final User client = new User();

    public static void main(String[] args) throws IOException {
        System.out.println("Client broadcast");

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

        Thread Client = new ClientContactDiscoveryController.EchoClient(socket);
        Client.start();

        //HOW TO DELAY BEFORE DISCONNECTION
        // To demonstrate sending the "end" message
        client.getContactList().forEach(u -> { try {
            System.out.println("Client disconnection...");
            sendEndConnection(u.getIPaddress(), socket);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        });


        //Server actions (wait for message from a Client)
        //Add client users to contact list
        new ServerContactDiscoveryController();
        DatagramSocket serverSocket = new DatagramSocket(4445);
        Thread Server = new ServerContactDiscoveryController.EchoServer(serverSocket);
        Server.start();
    }
}

