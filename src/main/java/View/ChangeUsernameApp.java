package View;

import Controller.ContactDiscovery.ServerUDP;
import Controller.ContactDiscovery.UserContactDiscovery;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class ChangeUsernameApp extends JFrame implements CustomListener{

    private final AtomicBoolean check= new AtomicBoolean(false);
    private JTextField usernameField;
    ContactListApp mainAppInterface;



    public ChangeUsernameApp() {
        setTitle("Change username");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JButton loginButton = new JButton("Change username");
        loginButton.addActionListener(e -> {
            try {
                onLoginButtonClick();
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);
    }


    private void onLoginButtonClick() throws IOException, InterruptedException {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UserContactDiscovery.inituser(username);
        UserContactDiscovery U = new UserContactDiscovery(username);
        // set atomic bool no_unique to false to reset it
        check.set(false);
        U.Action();

        TimeUnit.SECONDS.sleep(1);
        unique("Test");
        // todo create timer calling unique

    }
    @Override
    public void unique(String message) {
        // check if atomic bool not_unique if at false to continue
        if (!check.get()) {
            mainAppInterface = new ContactListApp();
            mainAppInterface.setVisible(true);
        }
    }


    @Override
    public void notUniquePopup(String message) {
        // set atomic bool no_unique to true
        check.set(true);
        JOptionPane.showMessageDialog(this, message, "Username not unique", JOptionPane.ERROR_MESSAGE);
        this.setVisible(true);
        // Show a popup with the received message

    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChangeUsernameApp loginApp = new ChangeUsernameApp();
            loginApp.setVisible(true);
            try {
                UserContactDiscovery.inituser("");
                ServerUDP.EchoServer server = UserContactDiscovery.Init();
                server.setDaemon(true);
                server.start();
                server.addActionListener(loginApp);
            } catch (SocketException | UnknownHostException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

