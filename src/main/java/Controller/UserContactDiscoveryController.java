package Controller;

import Model.User;

import java.io.IOException;
import java.util.List;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import static Controller.ClientContactDiscoveryController.*;
public class UserContactDiscoveryController {

    public static void main(String[] args) throws IOException{

        User user = new User();
        user.setUsername("Josua");
        user.setIPaddress(InetAddress.getLocalHost());
        user.setState(true);

        Thread Server = new ServerContactDiscoveryController.EchoServer(1555,user);


        //Client actions (send broadcast for contact discovery, change of username, end connection)
        //Add server users to contact list
        System.out.println("Client broadcast");
        List<InetAddress> broadcastList = listAllBroadcastAddresses();

        sendUsername(broadcastList,user);

        //Server actions (wait for message from a Client)
        //Add client users to contact list
        Server.start();


        //Client disconnection
        //TimeUnit.SECONDS.sleep(5);
        sendEndConnection(user);
    }
}

