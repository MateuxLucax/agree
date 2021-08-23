package gui.group;

import models.group.Group;

import javax.swing.*;

public class GroupManagementPanel {

    private JTextField tfName;
    private JPanel mainPanel;
    private JTextField tfPicture;
    private JButton btSave;
    private JButton btDelete;

    public GroupManagementPanel(Group group) {
        tfName.setText(group.getName());
        tfPicture.setText(group.getPicture());
    }

    public String getName() {
        return tfName.getText();
    }

    public String getPicture() {
        return tfPicture.getText();
    }

    public void onClickSave(Runnable onSave) {
        btSave.addActionListener(evt -> onSave.run());
    }

    public void onClickDelete(Runnable onDelete) {
        btDelete.addActionListener(evt -> onDelete.run());
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
