package gui;

import javax.swing.*;
import java.util.function.Consumer;

public class GroupManagementPanel {
    private JPanel mainPanel;
    private JButton btDelete;
    private JTextField tfNewName;
    private JButton btRename;

    public GroupManagementPanel(String initialName) {
        tfNewName.setText(initialName);
    }

    public void setRenameButtonListener(Consumer<String> onRename) {
        btRename.addActionListener(evt -> {
            // TODO deal with empty names
            String text = tfNewName.getText();
            onRename.accept(text);
        });
    }

    public void setDeleteButtonListener(Runnable onDelete) {
        btDelete.addActionListener(evt -> onDelete.run());
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
