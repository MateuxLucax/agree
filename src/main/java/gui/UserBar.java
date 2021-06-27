package gui;

import models.User;

import javax.swing.*;
import java.awt.*;

public class UserBar extends JPanel {

    private final JPanel buttonsPanel;

    public UserBar(User user) {
        setLayout(new BorderLayout());
        add(new JLabel(user.getNickname()), BorderLayout.PAGE_START);
        // TODO add profile picture icon on LINE_START
        buttonsPanel = new JPanel();
        add(buttonsPanel, BorderLayout.CENTER);
    }

    public void addButton(JButton btn) {
        buttonsPanel.add(btn);
    }

    public void removeButton(JButton btn) {
        buttonsPanel.remove(btn);
    }
}
