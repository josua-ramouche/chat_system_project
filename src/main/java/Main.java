import Controller.ServerContactDiscoveryController;
import Model.User;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static Controller.ClientContactDiscoveryController.*;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // User data
        User user = new User();
        //------------STRING TO MODIFY TO CHANGE INITIAL USERNAME---------------
        user.setUsername("Test1");
        user.setIPAddress(InetAddress.getLocalHost());
        user.setState(true);

        List<InetAddress> interfacesIP;
        interfacesIP = getInterfacesIP();

        Thread Server = new ServerContactDiscoveryController.EchoServer(user);

        // Find the broadcast addresses
        System.out.println("Broadcast address(es):");
        List<InetAddress> broadcastList = listAllBroadcastAddresses();

        // Client (initial sender) actions (send broadcast for contact discovery, change of username, end connection)
        sendUsername(broadcastList,user);

        // Server (connected user) actions (wait for message from a Client)
        Server.setDaemon(true);
        Server.start();

        // User asks for a change of username Test1 -> Test2
        TimeUnit.SECONDS.sleep(5);

        //------------STRING TO MODIFY TO CHANGE USERNAME AFTER THREE SECONDS---------------
        sendChangeUsername(user, "Test2");

        // User disconnection
        TimeUnit.SECONDS.sleep(5);
        sendEndConnection(user);
    }
}


