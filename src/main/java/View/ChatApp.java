package View;

import Model.User;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class ChatApp extends JFrame {
    private final JTextPane chatArea;
    private final JTextField messageField;

    private static final User me = new User("User1");
    private static final User partner = new User("User2");

    public ChatApp(User me, User partner) {
        setTitle("Chat with " + partner.getUsername());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Components
        JLabel userLabel = new JLabel("Chat with: " + partner.getUsername());
        JButton backButton = new JButton("Back");
        chatArea = new JTextPane();
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        StyledDocument doc = chatArea.getStyledDocument();

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
        sendButton.addActionListener(e -> sendMessage());

        // Add an action listener for the Enter key
        messageField.addActionListener(e -> sendMessage());

        // Set focus to the messageField
        messageField.requestFocusInWindow();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!Objects.equals(message, "")) {
            StyledDocument doc = chatArea.getStyledDocument();
            Style style = doc.addStyle("Style", null);

            StyleConstants.setForeground(style, Color.BLUE);
            try {
                doc.insertString(doc.getLength(), me.getUsername() + ": ", style);
                StyleConstants.setForeground(style, Color.BLACK);
                doc.insertString(doc.getLength(), message + "\n", style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            messageField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatApp chatApp = new ChatApp(me, partner);
            chatApp.setVisible(true);
        });
    }
}
