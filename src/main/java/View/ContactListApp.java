package View;

import Controller.ContactDiscovery.ServerUDP;
import Controller.ContactDiscovery.UserContactDiscovery;
import Model.ContactList;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class ContactListApp extends JFrame {

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
        initializeUI();
    }


    private void initializeUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 680);
        frame.setLocationRelativeTo(null);

        changeButton.setActionCommand("Change Username");
        changeButton.addActionListener(e -> {
            ChangeUsernameApp change = new ChangeUsernameApp();
            change.setVisible(true);
            System.out.println("Change username button clicked");
            this.dispose(); // Close the current window after opening the new one
        });




        backButton.setActionCommand("Disconnect");
        backButton.addActionListener(e -> {
            LoginApp disconnect = new LoginApp();
            disconnect.setVisible(true);
            System.out.println("Back button clicked");
            this.dispose(); // Close the current window after opening the new one
        });

        contactListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactListView.setSelectedIndex(0);
        contactListView.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Handle the selection of a contact
                System.out.println("Test");
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

    private void updateContactList() {
        contactListModel.clear();
        contactList = ContactList.getContacts();
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

    public static void main(String[] args) throws UnknownHostException {
        User user = new User();
        user.setUsername("Test1");
        user.setIPAddress(InetAddress.getLocalHost());
        user.setState(true);

        User contact1 = new User();
        contact1.setUsername("Contact1");
        contact1.setIPAddress(InetAddress.getByName("198.162.5.1"));
        contact1.setState(true);

        User contact2 = new User();
        contact2.setUsername("Contact2");
        contact2.setIPAddress(InetAddress.getByName("198.162.5.2"));
        contact2.setState(true);

        User contact3 = new User();
        contact3.setUsername("Contact3");
        contact3.setIPAddress(InetAddress.getByName("198.162.5.3"));
        contact3.setState(false);

        List<User> contactListTest = new ArrayList<>();
        contactListTest.add(contact1);
        contactListTest.add(contact2);
        contactListTest.add(contact3);

        ContactList.setContacts(contactListTest);

        SwingUtilities.invokeLater(() -> {
            ContactListApp contact = new ContactListApp();
            contact.setVisible(true);

        });

    }
}
