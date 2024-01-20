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
            LoginApp loginApp = new LoginApp();
            loginApp.setVisible(true);


            try {
                DatabaseController.createUserTable();
                DatabaseController.initConnection();
                UserContactDiscovery.inituser("");

                ServerUDP.EchoServer serverUDP = UserContactDiscovery.Init();
                serverUDP.start();
                serverUDP.addActionListener(loginApp);

                ClientUDP.addActionListener(loginApp);

                ServerTCP.listenTCP listenTCP = new ServerTCP.listenTCP();
                listenTCP.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}


