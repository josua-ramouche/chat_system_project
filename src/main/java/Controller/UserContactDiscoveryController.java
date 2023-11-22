package Controller;
import Model.User;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static Controller.ClientContactDiscoveryController.*;

public class UserContactDiscoveryController {
    public static void main(String[] args) throws IOException, InterruptedException {
        //User data
        User user = new User();
        user.setUsername("Test1");
        user.setIPaddress(InetAddress.getLocalHost());
        user.setState(true);

        List<InetAddress> interfacesIP;
        interfacesIP = getInterfacesIP();

        Thread Server = new ServerContactDiscoveryController.EchoServer(user, interfacesIP);

        //find the broadcasts addresses
        System.out.println("Broadcast address(es):");
        List<InetAddress> broadcastList = listAllBroadcastAddresses();

        //Client actions (send broadcast for contact discovery, change of username, end connection)
        //Add server users to contact list
        sendUsername(broadcastList,user);

        //Server actions (wait for message from a Client)
        //Add client users to contact list
        Server.setDaemon(true);
        Server.start();

        //the user ask for a change of username Test1 -> Test2
        TimeUnit.SECONDS.sleep(3);
        sendChangeUsername(user, "Test2");

        //Client disconnection
        TimeUnit.SECONDS.sleep(3);
        sendEndConnection(user);
    }
}


