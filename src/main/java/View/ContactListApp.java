package View;

import Controller.Chat.ClientTCP;
import Controller.ContactDiscovery.ClientUDP;
import Controller.Database.DatabaseController;
import Model.User;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import static Controller.Database.DatabaseController.printContactList;


public class ContactListApp extends JFrame implements CustomListener2{
    private final DefaultListModel<String> contactListModel;
    private final JList<String> contactListView;
    private List<User> contactList;
    private static User me;
    private ChangeUsernameApp changeUsernameApp = null;
    private JLabel nameLabel;

    private LoginApp loginApp;

    //Constructor and initialization of the components of the interface
    public ContactListApp(User me, LoginApp loginApp) throws InterruptedException {
        this.me =me;
        this.loginApp = loginApp;
        contactList = DatabaseController.getUsers();
        System.out.println("Contact list database : " + contactList.toString());
        JButton changeButton = new JButton("Change Username");
        JButton backButton = new JButton("Disconnect");
        contactListModel = new DefaultListModel<>();
        contactListView = new JList<>(contactListModel);
        List<User> users = DatabaseController.getUsers();
        addContactsToDisplayedList(users);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);

        //Components
        changeButton.setActionCommand("Change Username");
        changeButton.addActionListener(e -> {
            changeUsernameApp = new ChangeUsernameApp(me,this);
            changeUsernameApp.setVisible(true);
            System.out.println("Change username button clicked");
            this.setVisible(false);
        });

        backButton.setActionCommand("Disconnect");
        backButton.addActionListener(e -> {
                ClientTCP.endTCP();
            loginApp.setVisible(true);
            System.out.println("Disconnect button clicked");
            this.setVisible(false);
        });

        //Closing TCPs and UDP connections when we exit the app
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Window closing");
                try {
                    disconnectAndExit();
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        contactListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactListView.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Handle the selection of a contact
                try {
                    onContactSelection();
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        contactListView.setVisibleRowCount(5);
        nameLabel = new JLabel("Contact List of "  + me.getUsername());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        namePanel.add(nameLabel);
        JScrollPane listScrollPane = new JScrollPane(contactListView);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(changeButton);
        buttonPanel.add(backButton);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(namePanel, BorderLayout.NORTH);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        this.getContentPane().add(mainPanel);
        this.setVisible(true);
    }

    //Set our name in the Contact List app
    public void setMyUsername(String username) {
        me.setUsername(username);
        nameLabel.setText("Contact List of "  + me.getUsername());
    }

    public ChangeUsernameApp getChangeUsernameApp() {
        return changeUsernameApp;
    }

    private void disconnectAndExit() throws IOException, InterruptedException {
        ClientUDP.sendEndConnection();
        System.exit(0);
    }

    //Open a chat with the clicked user on the contact list
    private void onContactSelection() throws BadLocationException {
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
            ChatApp chat = new ChatApp(selectedContact, me, this);
            chat.setVisible(true);
            this.setVisible(false);
            ClientTCP.startConnection(selectedContact.getIPAddress(), 1556);
            contactListView.clearSelection();
        }
    }

    //Call a method to update the contact list (from the custom listener2)
    @Override
    public void updateContactList() {
        SwingUtilities.invokeLater(() -> {
            contactList = DatabaseController.getUsers();
            printContactList();
            try {
                addContactsToDisplayedList(contactList);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    //Update the contact list from the database
    private synchronized void addContactsToDisplayedList(List<User> users) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
            contactListModel.clear();
            for (User user : users) {
                contactListModel.addElement(user.getUsername() + " Online");
            }
        });
    }
}