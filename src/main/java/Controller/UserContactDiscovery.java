package Controller;

import Model.User;
import View.ContactListApp;
import View.LoginApp;

import javax.swing.*;
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
        List<InetAddress> interfacesIP = ClientContactDiscoveryController.getInterfacesIP();

    }

    public static ServerContactDiscoveryController.EchoServer Init() throws SocketException {

        // Start the server thread
        return new ServerContactDiscoveryController.EchoServer(temp);
    }

    public void Action() throws UnknownHostException, SocketException, InterruptedException {





        // Find the broadcast addresses
        List<InetAddress> broadcastList = ClientContactDiscoveryController.listAllBroadcastAddresses();

        // Send the username for contact discovery
        ClientContactDiscoveryController.sendUsername(broadcastList, temp);



        //wait(10000);
        // User disconnection
        //ClientContactDiscoveryController.sendEndConnection(temp);

        // Start the main application interface
        ContactListApp mainAppInterface = new ContactListApp(temp);
        mainAppInterface.setVisible(true);
        //add disconnection and change of username in it

    }




}
