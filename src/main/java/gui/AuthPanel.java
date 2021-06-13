package gui;

import app.UserSession;
import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import utils.AssetsUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AuthPanel {

    private JTextField tfName;
    private JPasswordField passwordField;
    private JButton btLogin;
    private JButton btCreateAccount;
    private JLabel lbWarning;
    private JPanel mainPanel;
    private JLabel title;

    private Runnable onSuccess;

    public void setSuccessListener(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    public AuthPanel() {
        Border border = title.getBorder();
        Border margin = new EmptyBorder(0,0,24,0);
        title.setIcon(new ImageIcon(Objects.requireNonNull(AssetsUtil.getImage(AssetsUtil.LOGO))));
        title.setBorder(new CompoundBorder(border, margin));

        btLogin.addActionListener(evt -> {
            lbWarning.setText("");
            String name = tfName.getText();
            String password = new String(passwordField.getPassword());
            passwordField.setText("");

            if (name.isEmpty() || password.isEmpty()) {
                lbWarning.setText("Username and Password are required!");
            } else {
                try {
                    UserSession.authenticate(name, password);
                    onSuccess.run();
                } catch (UnauthorizedUserException e) {
                    lbWarning.setText("Username or password incorrect!");
                }
            }
        });

        btCreateAccount.addActionListener(evt -> {
            lbWarning.setText("");
            String name = tfName.getText();
            String password = new String(passwordField.getPassword());
            passwordField.setText("");


            if (name.isEmpty() || password.isEmpty()) {
                lbWarning.setText("Username and Password are required!");
            } else {
                try {
                    UserSession.createAccount(name, password);
                    onSuccess.run();
                } catch (UnsafePasswordException e) {
                    lbWarning.setText("Unsafe password");
                } catch (NameAlreadyInUseException e) {
                    lbWarning.setText("Name " + name + " already in use");
                }
            }
        });
    }

    public JPanel getJPanel() {
        return mainPanel;
    }

}
