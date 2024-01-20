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
    public UserContactDiscovery (String enteredUsername) {
        this.enteredUsername=enteredUsername;
    }

    public static void inituser(String username) throws UnknownHostException, SocketException {
        // Check if the entered username is unique
        temp.setUsername(username);
        temp.setIPAddress(InetAddress.getLocalHost());
        temp.setState(true);
    }

    //Return a new EchoServerUDP
    public static ServerUDP.EchoServer Init() throws SocketException {
        return new ServerUDP.EchoServer(temp);
    }

    //Actions made when login button is clicked by the user
    public void Action() throws UnknownHostException, SocketException, InterruptedException {
        // Find the broadcast addresses
        List<InetAddress> broadcastList = ClientUDP.listAllBroadcastAddresses();
        // Send the username for contact discovery (broadcast)
        ClientUDP.sendUsername(broadcastList, temp);
    }
}
