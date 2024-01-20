import Controller.Chat.ServerTCP;
import Controller.ContactDiscovery.ClientUDP;
import Controller.ContactDiscovery.ServerUDP;
import Controller.ContactDiscovery.UserContactDiscovery;
import Controller.Database.DatabaseController;
import View.LoginApp;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //Start the application on the login app
            LoginApp loginApp = new LoginApp();
            loginApp.setVisible(true);
            try {
                //Creation of the User table if it does not exist already
                DatabaseController.createUserTable();
                //Put the state of all users to 0 (disconnected)
                DatabaseController.initConnection();
                UserContactDiscovery.inituser("");
                //Creation of the listener UDP thread + initialization
                ServerUDP.EchoServer serverUDP = UserContactDiscovery.Init();
                serverUDP.start();
                //Subscribing the loginApp to serverUDP and ClientUDP
                serverUDP.addActionListener(loginApp);
                ClientUDP.addActionListener(loginApp);
                //Creation of the ServerTCP + initialization
                ServerTCP.listenTCP listenTCP = new ServerTCP.listenTCP();
                listenTCP.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}


