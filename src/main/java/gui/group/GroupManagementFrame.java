package gui.group;

import models.group.Group;

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

    private final Group group;

    private final GroupManagementPanel panel;
    private Runnable onClose;

    public GroupManagementFrame(Group group)
    {
        this.group = group;

        setTitle(group.getName() + ": manage");

        panel = new GroupManagementPanel(group);
        setContentPane(panel.getJPanel());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    public String getName() {
        return panel.getName();
    }

    public String getPicture() {
        return panel.getPicture();
    }

    public void onClickSave(Runnable action) {
        panel.onClickSave(action);
    }

    public void onClickDelete(Runnable action) {
        panel.onClickDelete(action);
    }

    public boolean confirmDelete()
    {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to delete the group " + group.getName() + "?",
                "Delete group",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public void warnCouldNotDelete()
    {
        JOptionPane.showMessageDialog(
                this,
                "Couldn't delete the group " + group.getName(),
                "Delete group",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void warnInvalidInput()
    {
        JOptionPane.showMessageDialog(
                this,
                "Some of the input is invalid",
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

    public void warnCouldNotSave() {
        JOptionPane.showMessageDialog(
                this,
                "Could not update the group",
                "Delete group",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
