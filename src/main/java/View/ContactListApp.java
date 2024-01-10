package View;

import Controller.Chat.ClientTCP;
import Controller.ContactDiscovery.ClientUDP;
import Controller.Database.DatabaseController;
import Model.User;

import javax.swing.*;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Controller.Database.DatabaseController.printContactList;


public class ContactListApp extends JFrame implements CustomListener2{

    private DefaultListModel<String> contactListModel;
    private JList<String> contactListView;
    private List<User> contactList;

    private static User me;

    public ContactListApp(User me) throws InterruptedException {
        this.me =me;
        contactList = DatabaseController.getUsers();
        System.out.println("Contact list database : " + contactList.toString());

        JButton changeButton = new JButton("Change Username");
        // Added back button
        JButton backButton = new JButton("Disconnect");
        contactListModel = new DefaultListModel<>();
        contactListView = new JList<>(contactListModel);

        List<User> users = DatabaseController.getUsers();
        addContactsToDisplayedList(users);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 680);
        this.setLocationRelativeTo(null);


        changeButton.setActionCommand("Change Username");
        changeButton.addActionListener(e -> {
            ChangeUsernameApp change = new ChangeUsernameApp(me);
            change.setVisible(true);
            System.out.println("Change username button clicked");
            this.dispose();
        });

        backButton.setActionCommand("Disconnect");
        backButton.addActionListener(e -> {
            ClientUDP.sendEndConnection(me);
            LoginApp disconnect = new LoginApp();
            disconnect.setVisible(true);
            System.out.println("Back button clicked");
            this.dispose();
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectAndExit();
            }
        });



        contactListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactListView.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Handle the selection of a contact
                onContactSelection();
            }
        });
        contactListView.setVisibleRowCount(5);

        JLabel nameLabel = new JLabel("Contact List");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        namePanel.add(nameLabel);

        JScrollPane listScrollPane = new JScrollPane(contactListView);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(changeButton);
        buttonPanel.add(backButton); // Added back button to the panel

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(namePanel, BorderLayout.NORTH);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        this.setVisible(true);
    }

    private void disconnectAndExit() {
        // Disconnect and exit the application
        ClientUDP.sendEndConnection(me);
        System.exit(0);
    }

    private void onContactSelection() {
        // Index of the selected contact on the interface
        int index = contactListView.getSelectedIndex();
        System.out.println("INDEX: " + index);

        if(index!=-1) {
            // Get the actual user object from the contactList previously created
            User selectedContact = contactList.get(index);

            // Set the selectedIndex to the index from getSelectedIndex() method
            contactListView.setSelectedIndex(index);
            // Highlights the selected mission
            contactListView.ensureIndexIsVisible(index);


            ChatApp chat = new ChatApp(selectedContact, me);
            chat.setVisible(true);
            //frame.dispose();
            this.setVisible(false);
            ClientTCP.startConnection(selectedContact.getIPAddress(), 1556);
        }
    }

    @Override
    public void updateContactList() {
        SwingUtilities.invokeLater(() -> {
            printContactList();
            System.out.println("dans le listener2 : updatecontactlist");
            contactList = DatabaseController.getUsers();
            System.out.println("contact list size :" + contactList.size());
            printContactList();

            List<User> users = DatabaseController.getUsers();
            System.out.println("users !!!!!!!!: ");
            users.forEach(u -> {
                if (u.getState()) {
                    System.out.println("user : " + u.getUsername());
                }
            });
            try {
                addContactsToDisplayedList(users);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private synchronized void addContactsToDisplayedList(List<User> users) throws InterruptedException {
        contactListModel.clear();
        System.out.println("users2222222 !!!!!!!!: ");
        printContactList();
        //TimeUnit.SECONDS.sleep(1);
        SwingUtilities.invokeLater(() -> {
            for (User user : users) {
                contactListModel.addElement(user.getUsername() + " Online");
                contactListModel.elements();
            }
        });
    }

}