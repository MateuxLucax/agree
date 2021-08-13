package gui.group;

import javax.swing.*;
import java.util.function.Consumer;

public class GroupCreationPanel
{
    private JPanel mainPanel;
    private JTextField tfGroupName;
    private JButton btCreate;

    public void onClickCreate(Consumer<String> onCreation) {
        btCreate.addActionListener(evt -> onCreation.accept(tfGroupName.getText()));
    }

    public JPanel getJPanel() {
        return mainPanel;
    }
}
