package gui;

import javax.swing.*;
import java.util.function.Consumer;

public class CreateGroupPanel {
    private JPanel mainPanel;
    private JTextField tfGroupName;
    private JButton btCreate;

    public void onCreation(Consumer<String> onCreation) {
        btCreate.addActionListener(evt -> {
            String groupName = tfGroupName.getText();
            onCreation.accept(groupName);
        });
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
