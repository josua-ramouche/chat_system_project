package View;

import Model.ContactList;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ContactListApp extends JPanel{

    private final JFrame frame;
    private final DefaultListModel<String> contactListModel;
    private final JList<String> contactListView;
    private final JButton changeButton;
    private final User u;
    private List<User> contactList;

    public ContactListApp(User u){
        //Get information on the connected user
        this.u = u;
        //Get user's contact list
        contactList = ContactList.getContacts();

        //Create a new JFrame for the ContactListApp
        frame = new JFrame("Chat System");
        changeButton = new JButton("Change Username");
        contactListModel = new DefaultListModel<>();
        contactListView = new JList<>(contactListModel);

        addContactsToDisplayedList();

        initializeUI();
    }

    static class changeUsername implements ActionListener {
        //Action performed when a contact is selected
        User u;
        public changeUsername(User u) { this.u=u; }
        public void actionPerformed(ActionEvent e) {
//------------TO BE ADDED : OPEN CHANGE USERNAME INTERFACE WHEN IT'S DONE----------------------------------------------
            System.out.println("Test change username");
        }

    }

    private void initializeUI(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900,680);
        frame.setLocationRelativeTo(null);

        changeButton.setActionCommand("Change Username");
        changeButton.setEnabled(true);
        changeButton.addActionListener(new changeUsername(u));

        contactListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactListView.setSelectedIndex(0);
        contactListView.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
//------------TO BE ADDED : OPEN CONVERSATION WITH SELECTED USER WHEN CONVERSATION APP IS DONE--------------------------
                System.out.println("test");
            }
        });
        contactListView.setVisibleRowCount(5);

        // Create a label for interface name
        JLabel nameLabel = new JLabel("Contact List");
        nameLabel.setFont(new Font("Arial",Font.BOLD,15));

        // Create a panel for interface name
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        namePanel.add(nameLabel);

        // Create a panel for the contact list
        JScrollPane listScrollPane = new JScrollPane(contactListView);

        //Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(changeButton);

        // Create a panel for organizing components with GridLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        //Add panels to main panel
        mainPanel.add(namePanel, BorderLayout.NORTH);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);

    }

    private void updateContactList() {
        //Delete everything from displayed list
        contactListModel.clear();

        //Get updated contact list;
        contactList = ContactList.getContacts();
        addContactsToDisplayedList();
    }

    private void addContactsToDisplayedList() {
        //Contact list
        for (User user : contactList) {
            if(user.getState()) {
                contactListModel.addElement(user.getUsername() + " Online");
            }
            else{
                contactListModel.addElement(user.getUsername() + " Offline");
            }
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        // User data
        User user = new User();
        user.setUsername("Test1");
        user.setIPAddress(InetAddress.getLocalHost());
        user.setState(true);

        // Contacts data
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

        SwingUtilities.invokeLater(() -> new ContactListApp(user));
    }

}
