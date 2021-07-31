package gui.group;

import javax.swing.*;
import java.util.function.Consumer;

public class GroupManagementPanel {
    private JPanel mainPanel;
    private JButton btDelete;
    private JTextField tfNewName;
    private JButton btRename;

    public GroupManagementPanel(String groupName) {
        tfNewName.setText(groupName);
    }

    public void onClickRename(Consumer<String> onRename) {
        btRename.addActionListener(evt -> {
            // TODO deal with empty names, maybe involving a isNameFieldEmpty() public method?
            String text = tfNewName.getText();
            onRename.accept(text);
        });
    }

    public void onClickDelete(Runnable onDelete) {
        btDelete.addActionListener(evt -> onDelete.run());
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
