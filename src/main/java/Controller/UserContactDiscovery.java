package Controller;

import Model.User;
import View.LoginApp;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class UserContactDiscovery extends Component {
    String enteredUsername;
    public UserContactDiscovery (String enteredUsername)
    {
        this.enteredUsername=enteredUsername;
    }

    public void Action() throws UnknownHostException, SocketException {



        // Check if the entered username is unique
        User tempUser = new User();
        tempUser.setUsername(enteredUsername);
        tempUser.setIPAddress(InetAddress.getLocalHost());
        tempUser.setState(true);
        List<InetAddress> interfacesIP = ClientContactDiscoveryController.getInterfacesIP();

        // Start the server thread
        Thread serverThread = new ServerContactDiscoveryController.EchoServer(tempUser);


        // Find the broadcast addresses
        List<InetAddress> broadcastList = ClientContactDiscoveryController.listAllBroadcastAddresses();

        // Send the username for contact discovery
        ClientContactDiscoveryController.sendUsername(broadcastList, tempUser);

        serverThread.setDaemon(true);
        serverThread.start();


        // User disconnection
        ClientContactDiscoveryController.sendEndConnection(tempUser);

        // Start the main application interface
        //MainApplicationInterface mainAppInterface = new MainApplicationInterface(tempUser);
        //mainAppInterface.setVisible(true);
        //add disconnection and change of username in it

    }




}
