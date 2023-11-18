package Controller;

import Model.User;

import java.io.IOException;
import java.util.List;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import static Controller.ClientContactDiscoveryController.*;
public class UserContactDiscoveryController {

    public static void main(String[] args) throws IOException, InterruptedException {

        User user = new User();
        user.setUsername("Test");
        user.setIPaddress(InetAddress.getLocalHost());
        user.setState(true);

        Thread Server = new ServerContactDiscoveryController.EchoServer(user);


        //Client actions (send broadcast for contact discovery, change of username, end connection)
        //Add server users to contact list
        System.out.println("Broadcast address(es):");
        List<InetAddress> broadcastList = listAllBroadcastAddresses();

        sendUsername(broadcastList,user);

        //Server actions (wait for message from a Client)
        //Add client users to contact list
        Server.start();


        //Client disconnection
        TimeUnit.SECONDS.sleep(10);
        sendEndConnection(user);
    }
}

