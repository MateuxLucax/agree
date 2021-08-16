package gui.group;

/* Since the GroupCreationPanel was created with the IntelliJ IDEA
   GUI design tool, we can't just extract its logic to this class.
   The result is that this is just a thin wrapper around the panel
   that puts it in a frame.
 */


import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class GroupCreationFrame extends JFrame
{
    public GroupCreationPanel panel;
    public Runnable onClose;

    public GroupCreationFrame() {
        setTitle("Create a group");

        panel = new GroupCreationPanel();
        setContentPane(panel.getJPanel());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    public void onClickCreate(Consumer<String> action) {
        panel.onClickCreate(action);
    }

    public void warnCouldNotCreate() {
        JOptionPane.showMessageDialog(
                this,
                "Could not create the group",
                "Create",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void onClose(Runnable action) {
        onClose = action;
    }

    public void close() {
        if (onClose != null)
            onClose.run();
        dispose();
    }
}

