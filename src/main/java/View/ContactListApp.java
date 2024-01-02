package View;

import Controller.Database.DatabaseController;
import Model.ContactList;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static Controller.ContactDiscovery.ClientUDP.sendEndConnection;


public class ContactListApp extends JFrame implements CustomListener2{

    private final JFrame frame;
    private final DefaultListModel<String> contactListModel;
    private final JList<String> contactListView;
    private final JButton changeButton;
    private final JButton backButton; // Added back button
    private List<User> contactList;

    public ContactListApp() {
        contactList = ContactList.getContacts();

        frame = new JFrame("Chat System");
        changeButton = new JButton("Change Username");
        backButton = new JButton("Disconnect");
        contactListModel = new DefaultListModel<>();
        contactListView = new JList<>(contactListModel);

        addContactsToDisplayedList();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 680);
        frame.setLocationRelativeTo(null);


        changeButton.setActionCommand("Change Username");
        changeButton.addActionListener(e -> {
            ChangeUsernameApp change = new ChangeUsernameApp();
            change.setVisible(true);
            System.out.println("Change username button clicked");
            frame.dispose();
        });

        backButton.setActionCommand("Disconnect");
        backButton.addActionListener(e -> {
            //sendEndConnection(me);  comment ajouter le usr ici ??
            LoginApp disconnect = new LoginApp();
            disconnect.setVisible(true);
            System.out.println("Back button clicked");
            frame.dispose();
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

        ChatApp chat = new ChatApp(selectedContact);
        chat.setVisible(true);
    }

    @Override
    public void updateContactList() {
        System.out.println("dans le listener2 : fonctionne");
        contactListModel.clear();
        contactList = ContactList.getContacts();
        for (User user : contactList) {
            System.out.println("blablabla" + user);
        }
        addContactsToDisplayedList();
    }

    private void addContactsToDisplayedList() {
        for (User user : contactList) {
            if (user.getState()) {
                contactListModel.addElement(user.getUsername() + " Online");
            } else {
                contactListModel.addElement(user.getUsername() + " Offline");
            }
        }
    }

}
