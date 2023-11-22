package Controller;

import Model.User;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import static Controller.ClientContactDiscoveryController.*;
public class UserContactDiscoveryController {


    public static void main(String[] args) throws IOException, InterruptedException {

        User user = new User();
        user.setUsername("Test2");
        user.setIPaddress(InetAddress.getLocalHost());
        user.setState(true);


        List<InetAddress> interfacesIP = new ArrayList<>();
        interfacesIP = getInterfacesIP();

        Thread Server = new ServerContactDiscoveryController.EchoServer(user, interfacesIP);


        //Client actions (send broadcast for contact discovery, change of username, end connection)
        //Add server users to contact list
        System.out.println("Broadcast address(es):");
        List<InetAddress> broadcastList = listAllBroadcastAddresses();

        sendUsername(broadcastList,user);

        //Server actions (wait for message from a Client)
        //Add client users to contact list
        Server.setDaemon(true);
        Server.start();

        //TimeUnit.SECONDS.sleep(3);
        //sendChangeUsername(user, "Test4");

        //Client disconnection
        //TimeUnit.SECONDS.sleep(3);
        //sendEndConnection(user);
    }


}


