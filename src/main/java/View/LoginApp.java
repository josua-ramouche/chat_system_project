package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import Controller.ServerContactDiscoveryController;
import Controller.UserContactDiscovery;


public class LoginApp extends JFrame implements CustomListener{


    private JTextField usernameField;
    private boolean loginSuccessful = false;

    public LoginApp() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JButton loginButton = new JButton("Log In");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    onLoginButtonClick();
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);
    }


    private void onLoginButtonClick() throws IOException, InterruptedException {
        String enteredUsername = usernameField.getText();
        if (enteredUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UserContactDiscovery U = new UserContactDiscovery(enteredUsername);
        U.Action();
    }


    @Override
    public void showPopup(String message) {
        // Show a popup with the received message
        JOptionPane.showMessageDialog(this, message, "Username not unique", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginApp loginApp = new LoginApp();
                loginApp.setVisible(true);

                //ServerContactDiscoveryController.addActionListener(loginApp);
            }
        });
    }
}
