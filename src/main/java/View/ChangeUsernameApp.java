package View;

import Controller.ContactDiscovery.ClientUDP;
import Controller.ContactDiscovery.UserContactDiscovery;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChangeUsernameApp extends JFrame implements CustomListener {
    //TEST
    private final AtomicBoolean not_unique = new AtomicBoolean(false);
    private JTextField usernameField;

    private User oldme;

    public ChangeUsernameApp(User oldme) {
        this.oldme=oldme;
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

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            try {
                goBack();
            } catch (UnknownHostException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(new JLabel());
        panel.add(loginButton);
        panel.add(new JLabel());
        panel.add(backButton);

        add(panel);
    }
    private final List<CustomListener2> listeners2 = new ArrayList<>();
    public void addActionListener2(CustomListener2 listener) {
        listeners2.add(listener);
    }


    private void onLoginButtonClick() throws IOException, InterruptedException {

        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else {
            UserContactDiscovery.inituser(username);
            // set atomic bool no_unique to false to reset it
            not_unique.set(false);

            ClientUDP.sendChangeUsername(oldme,usernameField.getText());
            TimeUnit.SECONDS.sleep(1);
            unique();
        }
    }

    @Override
    public void unique() {

        // check if atomic bool not_unique if at false to continue
        if (!not_unique.get()) {
            ContactListApp contactListApp = null;
            oldme.setUsername(usernameField.getText());

            try {
                contactListApp = new ContactListApp(oldme);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            this.addActionListener2(contactListApp);
            contactListApp.setVisible(true);
            this.setVisible(false);

        }






    }

    @Override
    public void notUniquePopup(String message) {
        // set atomic bool no_unique to true
        not_unique.set(true);
        JOptionPane.showMessageDialog(this, message, "Username not unique", JOptionPane.ERROR_MESSAGE);
        this.setVisible(true);
        // Show a popup with the received message
    }

    @Override
    public synchronized void launchTest() {
        for (CustomListener2 listener2 : listeners2) {
            listener2.updateContactList();
            System.out.println("check ok listener2");
        }
    }

    private void goBack() throws UnknownHostException, InterruptedException {
        ContactListApp contactListApp = null;
        try {
            contactListApp = new ContactListApp(oldme);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        contactListApp.setVisible(true);
        this.setVisible(false);


    }


}