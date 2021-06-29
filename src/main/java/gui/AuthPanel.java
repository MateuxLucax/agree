package gui;

import utils.AssetsUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.util.Objects;
import java.util.function.BiConsumer;

public class AuthPanel {

    private JTextField tfName;
    private JPasswordField passwordField;
    private JButton btLogin;
    private JButton btCreateAccount;
    private JLabel lbWarning;
    private JPanel mainPanel;
    private JLabel title;

    private BiConsumer<String, String> onLoginAttempt;
    private BiConsumer<String, String> onRegistrationAttempt;

    public void onLogin(BiConsumer<String, String> onLoginAttempt) {
        this.onLoginAttempt = onLoginAttempt;
    }

    public void onRegistration(BiConsumer<String, String> onRegistrationAttempt) {
        this.onRegistrationAttempt = onRegistrationAttempt;
    }

    public AuthPanel() {
        Border border = title.getBorder();
        Border margin = new EmptyBorder(0,0,24,0);
        title.setIcon(new ImageIcon(Objects.requireNonNull(AssetsUtil.getImage(AssetsUtil.LOGO))));
        title.setBorder(new CompoundBorder(border, margin));

        btLogin.addActionListener(evt -> {
            String name = tfName.getText();
            String password = new String(passwordField.getPassword());
            passwordField.setText("");
            onLoginAttempt.accept(name, password);
        });

        btCreateAccount.addActionListener(evt -> {
            String name = tfName.getText();
            String password = new String(passwordField.getPassword());
            passwordField.setText("");
            onRegistrationAttempt.accept(name, password);
        });
    }

    public void warn(String text) {
        lbWarning.setText(text);
    }

    public JPanel getJPanel() {
        return mainPanel;
    }

}
