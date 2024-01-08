package View;

import Controller.Chat.ClientTCP;
import Controller.Chat.ServerTCP;
import Controller.ContactDiscovery.ClientUDP;
import Controller.Database.DatabaseController;
import Model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Controller.Database.DatabaseController.printContactList;

public class ContactListApp extends JFrame implements CustomListener2 {

    private final JFrame frame;
    private final DefaultTableModel contactTableModel;
    private final JTable contactTable;
    private List<User> contactList;

    private static User me;

    public ContactListApp(User me) throws InterruptedException {
        this.me = me;
        contactList = DatabaseController.getUsers();
        System.out.println("Contact list database : " + contactList.toString());

        frame = new JFrame("Chat System");
        JButton changeButton = new JButton("Change Username");
        JButton backButton = new JButton("Disconnect");

        // Initialize contactTableModel
        contactTableModel = new DefaultTableModel();
        contactTableModel.addColumn("Username");
        contactTableModel.addColumn("Status");

        contactTable = new JTable(contactTableModel);
        JScrollPane tableScrollPane = new JScrollPane(contactTable);

        List<User> users = DatabaseController.getUsers();
        addContactsToDisplayedList(users);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 680);
        frame.setLocationRelativeTo(null);

        changeButton.setActionCommand("Change Username");
        changeButton.addActionListener(e -> {
            ChangeUsernameApp change = new ChangeUsernameApp(me);
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

        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onContactSelection();
            }
        });

        JLabel nameLabel = new JLabel("Contact List");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        namePanel.add(nameLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(changeButton);
        buttonPanel.add(backButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(namePanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private void disconnectAndExit() {
        ServerTCP.ClientHandler.endConnection();
        ClientUDP.sendEndConnection(me);
        System.exit(0);
    }

    private void onContactSelection() {
        int index = contactTable.getSelectedRow();
        System.out.println("INDEX: " + index);

        if (index != -1) {
            User selectedContact = contactList.get(index);
            contactTable.getSelectionModel().setSelectionInterval(index, index);
            contactTable.scrollRectToVisible(contactTable.getCellRect(index, 0, true));

            ChatApp chat = new ChatApp(selectedContact, me);
            chat.setVisible(true);
            frame.setVisible(false);
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
        System.out.println("users2222222 !!!!!!!!: ");
        printContactList();
        TimeUnit.SECONDS.sleep(1);
        SwingUtilities.invokeLater(() -> {
            contactTableModel.setRowCount(0);
            for (User user : users) {
                String status = user.getState() ? "Online" : "Offline";
                contactTableModel.addRow(new Object[]{user.getUsername(), status});
            }
        });
    }
}
