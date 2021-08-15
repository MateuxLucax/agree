package gui.group;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;


/* Since the GroupManagementPanel was made with the IntelliJ IDEA
   GUI design tool, we can't just extract the logic in the class
   to the frame. The result is that this class is a very thin wrapper
   around the panel that just puts it into a (pop up) frame.
 */

public class GroupManagementFrame extends JFrame {

    private final String groupName;

    private final GroupManagementPanel panel;
    private Runnable onClose;

    public GroupManagementFrame(String groupName)
    {
        this.groupName = groupName;

        setTitle(groupName + ": manage");

        panel = new GroupManagementPanel(groupName);
        setContentPane(panel.getJPanel());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    public void onClickRename(Consumer<String> action)
    {
        panel.onClickRename(action);
    }

    public void onClickDelete(Runnable action)
    {
        panel.onClickDelete(action);
    }

    public boolean confirmDelete()
    {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to delete the group " + groupName + "?",
                "Delete group",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public void warnCouldNotDelete()
    {
        JOptionPane.showMessageDialog(
                this,
                "Couldn't delete the group " + groupName,
                "Delete group",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void warnCouldNotRename(String newName)
    {
        JOptionPane.showMessageDialog(
                this,
                "Couldn't rename the group " + groupName + " to \"" + newName + "\"",
                "Delete group",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void onClose(Runnable action)
    {
        onClose = action;
    }

    public void close()
    {
        if (onClose != null)
            onClose.run();
        dispose();
    }
}
