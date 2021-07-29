package gui;

import javax.swing.*;
import java.util.function.Consumer;


/* Since the GroupManagementPanel was made with the IntelliJ IDEA
   GUI design tool, we can't just extract the logic in the class
   to the frame. The result is that this class is a very thin wrapper
   around the panel that just puts it into a (pop up) frame.
 */

public class GroupManagementFrame extends PopUpFrame {

    private GroupManagementPanel panel;

    public GroupManagementFrame(String groupName, JButton btnThatOpenedTheFrame)
    {
        super(btnThatOpenedTheFrame);
        panel = new GroupManagementPanel(groupName);
        setContentPane(panel.getJPanel());
    }

    public void onClickRename(Consumer<String> onRename)
    {
        panel.onRename(newName -> {
            onRename.accept(newName);
            close();
        });
    }

    public void onClickDelete(Runnable onDelete)
    {
        panel.onDelete(() -> {
            onDelete.run();
            close();
        });
    }
}
