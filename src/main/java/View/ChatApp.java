package View;

import Controller.Chat.ClientTCP;
import Controller.Database.DatabaseController;
import Model.Message;
import Model.User;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Objects;
import java.util.List;

public class ChatApp extends JFrame {
    private static JTextPane chatArea = null;
    private final JTextField messageField;

    private static User partner = null;

    public ChatApp(User partner) {
        ChatApp.partner =partner;
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
            ContactListApp contact = new ContactListApp();
            contact.setVisible(true);
            dispose();
        });

        // send button action
        sendButton.addActionListener(e -> sendMessageTCP());

        // Add an action listener for the Enter key
        messageField.addActionListener(e -> sendMessageTCP());

        // Set focus to the messageField
        messageField.requestFocusInWindow();
    }

    private void sendMessageTCP() {
        String message = messageField.getText();
        if (!Objects.equals(message, "")) {
            ClientTCP.sendMessage();
            DatabaseController.saveSentMessage(DatabaseController.getUserID(partner),message);
            messageField.setText("");
        }

        List<Message> messages =DatabaseController.getMessages(DatabaseController.getUserID(partner));
        PrintHistory(messages);
    }



    //print all the messsages when i send a message or when i receive a message (tcp)
    public static void PrintHistory(List<Message> messages) {
        messages.forEach(msg -> {
            StyledDocument doc = chatArea.getStyledDocument();
            Style style = doc.addStyle("Style", null);

            if (msg.getSender().equals(partner)) { //messages i received
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
            else { //my messages
                StyleConstants.setForeground(style, Color.RED);
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
    }

}
