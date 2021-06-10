package gui;

import data.UserDataAccess;
import exceptions.IncorrectPasswordException;
import exceptions.NameAlreadyInUseException;
import exceptions.UnsafePasswordException;

import javax.swing.*;

public class AuthPanel {

    private JTextField tfName;
    private JPasswordField passwordField;
    private JButton btLogin;
    private JButton btCreateAccount;
    private JLabel lbWarning;
    private JPanel panel;

    public AuthPanel() {
        btLogin.addActionListener(evt -> {
            try {
                String name = tfName.getText();
                String password = new String(passwordField.getPassword());
                UserDataAccess.getInstance().authenticate(name, password);
            } catch (IncorrectPasswordException e) {
                lbWarning.setText("Incorrect password for " + e.getUsername());
            }
        });

        btCreateAccount.addActionListener(evt -> {
            try {
                String name = tfName.getText();
                String password = new String(passwordField.getPassword());
                UserDataAccess.getInstance().validateNewAccount(name, password);
            } catch (UnsafePasswordException e) {
                lbWarning.setText("Unsafe password");
            } catch (NameAlreadyInUseException e) {
                lbWarning.setText("Name " + e.getName() + " already in use");
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

}
