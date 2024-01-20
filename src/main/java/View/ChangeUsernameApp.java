package View;

import Controller.ContactDiscovery.ClientUDP;
import Controller.ContactDiscovery.UserContactDiscovery;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChangeUsernameApp extends JFrame implements CustomListener {
    private final AtomicBoolean not_unique = new AtomicBoolean(false);
    private JTextField usernameField;
    private ContactListApp mainAppInterface;
    private final User oldme;
    private final List<CustomListener2> listeners2 = new ArrayList<>();

    //Constructor
    public ChangeUsernameApp(User oldme, ContactListApp contactListApp) {
        this.oldme=oldme;
        setTitle("Change username");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        mainAppInterface = contactListApp;
        initComponents();
    }

    //Initialization of the interface components
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JButton changeButton = new JButton("Change username");
        changeButton.addActionListener(e -> {
            try {
                onChangeButtonClick();
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

        // Add ActionListener to usernameField to listen for Enter key
        usernameField.addActionListener(e -> {
            try {
                onChangeButtonClick();
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    disconnectAndExit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(new JLabel());
        panel.add(changeButton);
        panel.add(new JLabel());
        panel.add(backButton);

        add(panel);
    }

    public void addActionListener2(CustomListener2 listener) {
        listeners2.add(listener);
    }


    private void onChangeButtonClick() throws IOException, InterruptedException {

        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            UserContactDiscovery.inituser(username);
            // set atomic bool no_unique to false to reset it
            not_unique.set(false);

            ClientUDP.sendChangeUsername(oldme,usernameField.getText());

            try {
                Thread.sleep(2100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            unique();
        }
    }

    @Override
    public void unique() {

        // check if atomic bool not_unique if at false to continue
        if (!not_unique.get()) {
            oldme.setUsername(usernameField.getText());
            oldme.setState(true);

            this.addActionListener2(mainAppInterface);
            mainAppInterface.setMyUsername(oldme.getUsername());
            mainAppInterface.setVisible(true);

            this.setVisible(false);
        }
        launchTest();

    }

    @Override
    public void notUniquePopup(String message) {  //unused here
        // set atomic bool no_unique to true
        System.out.println("not unique du change username avant");
        if (!not_unique.get()) {
            System.out.println("not unique du change username apres");

            not_unique.set(true);
            JOptionPane.showMessageDialog(this, "Username not unique", "Username not unique", JOptionPane.ERROR_MESSAGE);
            this.setVisible(true);
        }
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
        //mainAppInterface = new ContactListApp(oldme);
        mainAppInterface.setVisible(true);
        this.setVisible(false);
    }

    private void disconnectAndExit() throws IOException {
        // Disconnect and exit the application
        //ServerTCP.ClientHandler.endConnection();
        ClientUDP.sendEndConnection();
        System.exit(0);
    }


}