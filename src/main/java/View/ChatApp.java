package View;

import Controller.Chat.ClientTCP;
import Controller.Chat.ServerTCP;
import Controller.ContactDiscovery.ClientUDP;
import Controller.Database.DatabaseController;
import Model.Message;
import Model.User;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;
import java.util.List;

public class ChatApp extends JFrame {
    private static JTextPane chatArea = null;
    private final JTextField messageField;

    private static User partner = null;
    private static User me =null;

    public ChatApp(User partner, User me) {
        ChatApp.partner=partner;
        ChatApp.me=me;
        setTitle("Chat with " + partner.getUsername());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Components
        JLabel userLabel = new JLabel("Chat with: " + partner.getUsername());
        JButton backButton = new JButton("Back");
        chatArea = new JTextPane();
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        topPanel.add(userLabel);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);




        // back button action
        backButton.addActionListener(e -> {
            try {
                ContactListApp.getInstance().setVisible(true);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            this.setVisible(false);
        });

        addWindowListener(new WindowAdapter() {
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
        sendButton.addActionListener(e -> sendMessageTCP());

        // Add an action listener for the Enter key
        messageField.addActionListener(e -> sendMessageTCP());

        // Set focus to the messageField
        messageField.requestFocusInWindow();

        List<Message> messages = DatabaseController.getMessages(DatabaseController.getUserID(partner));
        PrintHistory(messages);

        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void disconnectAndExit() throws IOException {
        // Disconnect and exit the application
        //ServerTCP.ClientHandler.endConnection();
        ClientUDP.sendEndConnection();
        System.exit(0);
    }


    private void sendMessageTCP() {
        String message = messageField.getText();
        if (!Objects.equals(message, "")) {
            ClientTCP.sendMessage(message);
            System.out.println("Message send");
            DatabaseController.saveSentMessage(DatabaseController.getUserID(partner),message);
            messageField.setText("");
        }

        List<Message> messages = DatabaseController.getMessages(DatabaseController.getUserID(partner));
        PrintHistory(messages);
    }



    //print all the messsages when i send a message or when i receive a message (tcp)
    public static void PrintHistory(List<Message> messages) {
        chatArea.setText("");
        messages.forEach(msg -> {
            StyledDocument doc = chatArea.getStyledDocument();
            Style style = doc.addStyle("Style", null);
            InetAddress senderip = msg.getSender().getIPAddress();


            Style leftAlignStyle = doc.addStyle("LeftAlignStyle", style);
            StyleConstants.setAlignment(leftAlignStyle, StyleConstants.ALIGN_LEFT);
            Style rightAlignStyle = doc.addStyle("RightAlignStyle", style);
            StyleConstants.setAlignment(rightAlignStyle, StyleConstants.ALIGN_RIGHT);

            if (senderip == null) { //me
                StyleConstants.setForeground(style, Color.RED);
                int length = doc.getLength();
                doc.setParagraphAttributes(length - msg.getContent().length(), length,
                        leftAlignStyle, false);
                try {
                    doc.insertString(doc.getLength(), msg.getDate() + " ", style);
                    doc.insertString(doc.getLength(), "Me" + ": ", style);
                    StyleConstants.setForeground(style, Color.BLACK);
                    doc.insertString(doc.getLength(), msg.getContent() + "\n", style);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            } else { //partner
                StyleConstants.setForeground(style, Color.BLUE);
                int length = doc.getLength();
                doc.setParagraphAttributes(length - msg.getContent().length(), length,
                        rightAlignStyle, false);
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

}
