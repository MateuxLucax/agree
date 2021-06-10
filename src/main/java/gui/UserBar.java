package gui;

import models.User;

import javax.swing.*;

// TODO add profile picture

public class UserBar {
    private JPanel main;
    private JLabel lbName;

    public UserBar(User user) {
        lbName.setText(user.getNickname());
    }

    public JPanel getPanel() {
        return main;
    }

}
