package View;

import Controller.Chat.ClientTCP;
import Controller.Chat.ServerTCP;
import Controller.ContactDiscovery.ClientUDP;
import Controller.Database.DatabaseController;
import Model.User;

import javax.swing.*;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static Model.ContactList.printContactList;


public class ContactListApp extends JFrame implements CustomListener2{

    private final JFrame frame;
    private final DefaultListModel<String> contactListModel;
    private final JList<String> contactListView;
    private List<User> contactList;

    private static User me;

    public ContactListApp(User me) {
        this.me =me;
        contactList = DatabaseController.getUsers();
        System.out.println("Contact list database : " + contactList.toString());

        frame = new JFrame("Chat System");
        JButton changeButton = new JButton("Change Username");
        // Added back button
        JButton backButton = new JButton("Disconnect");
        contactListModel = new DefaultListModel<>();
        contactListView = new JList<>(contactListModel);

        List<User> users = DatabaseController.getUsers();
        addContactsToDisplayedList(users);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 680);
        frame.setLocationRelativeTo(null);


        changeButton.setActionCommand("Change Username");
        changeButton.addActionListener(e -> {
            //ClientUDP.sendEndConnection(me);
            ChangeUsernameApp change = new ChangeUsernameApp();
            change.setVisible(true);
            System.out.println("Change username button clicked");
            frame.dispose();
        });

        backButton.setActionCommand("Disconnect");
        backButton.addActionListener(e -> {
            ClientUDP.sendEndConnection(me);
            LoginApp disconnect = new LoginApp();
            disconnect.setVisible(true);
            System.out.println("Back button clicked");
            frame.dispose();
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

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private void disconnectAndExit() {
        // Disconnect and exit the application
        ServerTCP.ClientHandler.endConnection();
        ClientUDP.sendEndConnection(me);
        System.exit(0);
    }

    private void onContactSelection() {
        // Index of the selected contact on the interface
        int index = contactListView.getSelectedIndex();

        // Get the actual user object from the contactList previously created
        User selectedContact = contactList.get(index);

        // Set the selectedIndex to the index from getSelectedIndex() method
        contactListView.setSelectedIndex(index);
        // Highlights the selected mission
        contactListView.ensureIndexIsVisible(index);

        int id = DatabaseController.getUserID(selectedContact);
        DatabaseController.createChatTable(id);

        ChatApp chat = new ChatApp(selectedContact,me);
        chat.setVisible(true);
        //frame.dispose();
        frame.setVisible(false);
        ClientTCP.startConnection(selectedContact.getIPAddress(),1556);
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
            addContactsToDisplayedList(users);
        });
    }


    private synchronized void addContactsToDisplayedList(List<User> users) {
        System.out.println("users2222222 !!!!!!!!: ");
        users.forEach(u -> {
            if (u.getState()) {
                System.out.println("user : " + u.getUsername());
            }
        });

        SwingUtilities.invokeLater(() -> {
            contactListModel.clear();
            for (User user : users) {
                if (user.getState() && !user.getUsername().equals("")) {
                    contactListModel.addElement(user.getUsername() + " Online");
                }
            }
        });
    }

}
