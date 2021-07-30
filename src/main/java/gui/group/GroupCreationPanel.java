package gui.group;

import javax.swing.*;
import java.util.function.Consumer;

public class GroupCreationPanel
{
    private JPanel mainPanel;
    private JTextField tfGroupName;
    private JButton btCreate;

    public void onCreation(Consumer<String> onCreation) {
        btCreate.addActionListener(evt -> {
            String groupName = tfGroupName.getText();
            onCreation.accept(groupName);
            tfGroupName.setText("");
        });
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
