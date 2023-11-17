package Controller;

import Model.User;

import java.io.IOException;
import java.util.List;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import static Controller.ClientContactDiscoveryController.*;
public class UserContactDiscoveryController {

    public static void main(String[] args) throws IOException, InterruptedException {

        User user = new User();
        user.setUsername("Lucile");
        user.setIPaddress(InetAddress.getLocalHost());
        user.setState(true);

        new ClientContactDiscoveryController(user);
        DatagramSocket socket = new DatagramSocket();
        Thread Client = new EchoClient(socket);
        Thread Server = new ServerContactDiscoveryController.EchoServer(socket, user);


        //Client actions (send broadcast for contact discovery, change of username, end connection)
        //Add server users to contact list
        System.out.println("Client broadcast");
        List<InetAddress> broadcastList = listAllBroadcastAddresses();


        socket.setBroadcast(true);

        for (InetAddress inetAddress : broadcastList) {
            try {
                System.out.println("Broadcast address : " + inetAddress);
                broadcast(user.getUsername(), inetAddress, socket);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        Client.start();


        //Server actions (wait for message from a Client)
        //Add client users to contact list
        Server.start();


        //Client disconnection
        TimeUnit.SECONDS.sleep(5);
        sendEndConnection(socket,Client);
    }
}

