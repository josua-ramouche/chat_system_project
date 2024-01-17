package View;

import Controller.ContactDiscovery.UserContactDiscovery;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class LoginApp extends JFrame implements CustomListener{

    private final AtomicBoolean not_unique= new AtomicBoolean(false);
    private JTextField usernameField;

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
        loginButton.addActionListener(e -> {
            try {
                onLoginButtonClick();
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        });

        // Add ActionListener to usernameField to listen for Enter key
        usernameField.addActionListener(e -> {
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
        }
        else {

            UserContactDiscovery.inituser(username);
            UserContactDiscovery U = new UserContactDiscovery(username);
            // set atomic bool no_unique to false to reset it
            not_unique.set(false);
            U.Action();

            TimeUnit.SECONDS.sleep(1);

            // Check if the login is successful before calling unique
            if (!not_unique.get()) {
                unique();
            }
        }
    }

    private List<CustomListener2> listeners2 = new ArrayList<>();
    public void addActionListener2(CustomListener2 listener) {
        listeners2.add(listener);
    }


    @Override
    public void unique() throws UnknownHostException, InterruptedException {

        // check if atomic bool not_unique if at false to continue
        if (!not_unique.get()) {
            User me = new User();
            me.setUsername(usernameField.getText());
            me.setIPAddress(InetAddress.getLocalHost());
            me.setState(true);

            ContactListApp mainAppInterface = new ContactListApp(me);
            this.addActionListener2(mainAppInterface);


            mainAppInterface.setVisible(true);
            launchTest();
            this.setVisible(false);
        }


    }
    @Override
    public synchronized void launchTest() {
        for (CustomListener2 listener2 : listeners2) {
            listener2.updateContactList();
            System.out.println("check ok listener2");
        }
    }


    @Override
    public void notUniquePopup(String message) {
        // set atomic bool not_unique to true
        not_unique.set(true);
        JOptionPane.showMessageDialog(this, message, "Username not unique", JOptionPane.ERROR_MESSAGE);
        this.setVisible(true);
        // Show a popup with the received message

    }
}