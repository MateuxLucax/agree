package gui;

import models.User;

import javax.swing.*;

public class SettingsPanel {

    private JPanel mainPanel;
    private JTextField tfPicture;
    private JLabel lbPicture;
    private JButton btSave;

    public SettingsPanel(User user) {
        tfPicture.setText(user.getPicture());
    }

    public void onClickSave(Runnable action) {
        btSave.addActionListener(e -> action.run());
    }

    public String getPicture() {
        return tfPicture.getText();
    }

    public void warnCouldNotUpdate() {
        JOptionPane.showMessageDialog(
                mainPanel,
                "Could not update your settings",
                "Save",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void alertUpdateSuccessful() {
        JOptionPane.showMessageDialog(
                mainPanel,
                "Your settings were updated successfully",
                "Save",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}
