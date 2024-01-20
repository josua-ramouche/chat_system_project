package View;

import Controller.Chat.ClientTCP;
import Controller.Chat.ServerTCP;
import Controller.ContactDiscovery.ClientUDP;
import Controller.Database.DatabaseController;
import Model.Message;
import Model.User;

import javax.swing.*;
import javax.swing.text.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;
import java.util.List;

public class ChatApp extends JFrame {
    private static JTextPane chatArea = new JTextPane();
    private final JTextField messageField;
    private static User partner;

    private static User me =null;

    private static int clientDisconnected=1;



    public ChatApp(User partner, User me, ContactListApp contactListApp) throws BadLocationException {
        this.partner = partner;
        ChatApp.me=me;
        chatArea.setEditable(false);

        clientDisconnected=1;
        setTitle("Chat with " + partner.getUsername());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Components
        JLabel userLabel = new JLabel("Me: " + me.getUsername() + " chat with: " + partner.getUsername());
        JButton backButton = new JButton("Back");
        JButton sendButton = new JButton("Send");

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        topPanel.add(userLabel);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        messageField = new JTextField();

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);




        // back button action
        backButton.addActionListener(e -> {
            contactListApp.setVisible(true);
            this.setVisible(false);
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



        // send button action
        sendButton.addActionListener(e -> {
            try {
                sendMessageTCP();
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        });


        // Add an action listener for the Enter key
        messageField.addActionListener(e -> {
            try {
                sendMessageTCP();
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Set focus to the messageField
        messageField.requestFocusInWindow();

        PrintHistory(1);

        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void disconnectAndExit() throws IOException {
        // Disconnect and exit the application
        //ServerTCP.ClientHandler.endConnection();
        //ClientTCP.stopConnection();
        ClientUDP.sendEndConnection();
        System.exit(0);
    }


    private void sendMessageTCP() throws BadLocationException {
        if (clientDisconnected!=-1) {
            String message = messageField.getText();
            if (!Objects.equals(message, "")) {
                ClientTCP.sendMessage(message);
                System.out.println("Message send: " + message);
                DatabaseController.saveSentMessage(DatabaseController.getUserID(partner), message);
                messageField.setText("");
            }
            PrintHistory(DatabaseController.getUserID(partner));
        }

    }




    //print all the messsages when i send a message or when i receive a message (tcp)
    public synchronized static void PrintHistory(int iddisconnected) throws BadLocationException {
        //id disconected=-1 id client connected
        //if client disconnected then return its id

        if (iddisconnected!=-1 ) {
            if (partner != null) {
                chatArea.setText("");

                int clientid = DatabaseController.getUserID(partner);
                List<Message> messages = DatabaseController.getMessages(clientid);

                System.out.println("Partner :" + partner.getUsername());

                messages.forEach(msg -> {
                    StyledDocument doc = chatArea.getStyledDocument();
                    Style style = doc.addStyle("Style", null);
                    InetAddress senderip = msg.getSender().getIPAddress();


                    if (senderip == null) { //me

                        StyleConstants.setForeground(style, Color.RED);

                        try {
                            doc.insertString(doc.getLength(), msg.getDate() + " ", style);
                            doc.insertString(doc.getLength(), "Me" + ": ", style);
                            StyleConstants.setForeground(style, Color.BLACK);
                            doc.insertString(doc.getLength(), msg.getContent() + "\n", style);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    } else if (partner.getUsername().equals(msg.getSender().getUsername())) { //partner

                        StyleConstants.setForeground(style, Color.BLUE);

                        try {
                            doc.insertString(doc.getLength(), msg.getDate() + " ", style);
                            doc.insertString(doc.getLength(), msg.getSender().getUsername() + ": ", style);
                            StyleConstants.setForeground(style, Color.BLACK);
                            doc.insertString(doc.getLength(), msg.getContent() + "\n", style);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Set the scroll position to the bottom
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
            if (clientDisconnected==-1) {
                StyledDocument doc = chatArea.getStyledDocument();
                Style style = doc.addStyle("Style", null);
                StyleConstants.setForeground(style, Color.GREEN);
                doc.insertString(doc.getLength(), "USER DISCONNECTED" + "\n", style);
                doc.insertString(doc.getLength(), "You can't send another message" + "\n", style);
            }
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        }
        else {
            clientDisconnected=-1;

            StyledDocument doc = chatArea.getStyledDocument();
            Style style = doc.addStyle("Style", null);
            StyleConstants.setForeground(style, Color.GREEN);
            doc.insertString(doc.getLength(), "USER DISCONNECTED" + "\n", style);
            doc.insertString(doc.getLength(), "You can't send another message" + "\n", style);
        }
    }

}