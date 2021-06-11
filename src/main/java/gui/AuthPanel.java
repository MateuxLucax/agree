package gui;

import data.UserDataAccess;
import exceptions.IncorrectPasswordException;
import exceptions.NameAlreadyInUseException;
import exceptions.UnsafePasswordException;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AuthPanel {

    private JTextField tfName;
    private JPasswordField passwordField;
    private JButton btLogin;
    private JButton btCreateAccount;
    private JLabel lbWarning;
    private JPanel mainPanel;

    private Consumer<String> onLogin;
    private BiConsumer<String, String> onRegistration;

    public void setLoginListener(Consumer<String> onLogin) {
        this.onLogin = onLogin;
    }

    public void setRegistrationListener(BiConsumer<String, String> onRegistration) {
        this.onRegistration = onRegistration;
    }

    public AuthPanel() {
        btLogin.addActionListener(evt -> {
            try {
                String name = tfName.getText();
                String password = new String(passwordField.getPassword());
                UserDataAccess.getInstance().authenticate(name, password);
                onLogin.accept(name);
            } catch (IncorrectPasswordException e) {
                lbWarning.setText("Incorrect password for " + e.getUsername());
            }
        });

        btCreateAccount.addActionListener(evt -> {
            try {
                String name = tfName.getText();
                String password = new String(passwordField.getPassword());
                UserDataAccess.getInstance().validateNewAccount(name, password);
                onRegistration.accept(name, password);
            } catch (UnsafePasswordException e) {
                lbWarning.setText("Unsafe password");
            } catch (NameAlreadyInUseException e) {
                lbWarning.setText("Name " + e.getName() + " already in use");
            }
        });
    }

    public JPanel getJPanel() {
        return mainPanel;
    }

}
