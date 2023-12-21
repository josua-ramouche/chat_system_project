package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import Controller.ContactDiscovery.*;


public class LoginApp extends JFrame implements CustomListener{


    private JTextField usernameField;
    private final boolean loginSuccessful = false;
    ContactListApp mainAppInterface;



    public LoginApp() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JButton loginButton = new JButton("Log In");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    onLoginButtonClick();
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
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
        U.Action();

    }
    @Override
    public void unique(String message) {
        mainAppInterface = new ContactListApp();
        mainAppInterface.setVisible(true);
    }


    @Override
    public void notUniquePopup(String message) {
        JOptionPane.showMessageDialog(this, message, "Username not unique", JOptionPane.INFORMATION_MESSAGE);
        this.setVisible(true);
        // Show a popup with the received message
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginApp loginApp = new LoginApp();
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
            }
        });
    }
}
