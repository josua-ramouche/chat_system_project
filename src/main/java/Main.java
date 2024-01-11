import Controller.Chat.ChatController;
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
                //serverUDP.setDaemon(true);
                serverUDP.start();
                serverUDP.addActionListener(loginApp);

                ClientUDP.addActionListener(loginApp);

                ChatController.listenTCP serverTCP = new ChatController.listenTCP();
                serverTCP.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}


