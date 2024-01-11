package Controller.ContactDiscovery;

import Model.User;

import java.awt.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class UserContactDiscovery extends Component {
    String enteredUsername;

    static User temp = new User();
    public UserContactDiscovery (String enteredUsername)
    {
        this.enteredUsername=enteredUsername;
    }

    public static void inituser(String username) throws UnknownHostException, SocketException {
        // Check if the entered username is unique
        temp.setUsername(username);
        temp.setIPAddress(InetAddress.getLocalHost());
        temp.setState(true);

    }

    public static ServerUDP.EchoServer Init() throws SocketException {

        // Start the server thread
        return new ServerUDP.EchoServer(temp);
    }

    public void Action() throws UnknownHostException, SocketException, InterruptedException {

        // Find the broadcast addresses
        List<InetAddress> broadcastList = ClientUDP.listAllBroadcastAddresses();

        // Send the username for contact discovery
        ClientUDP.sendUsername(broadcastList, temp);

    }




}
