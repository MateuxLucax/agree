package gui.group;

/* Since the GroupCreationPanel was created with the IntelliJ IDEA
   GUI design tool, we can't just extract its logic to this class.
   The result is that this is just a thin wrapper around the panel
   that puts it in a frame.
 */

import gui.PopUpFrame;

import javax.swing.*;
import java.util.function.Consumer;

public class GroupCreationFrame extends PopUpFrame
{
    public GroupCreationPanel panel;

    public GroupCreationFrame(JButton btnThatOpenedTheFrame)
    {
        super(btnThatOpenedTheFrame);
        panel = new GroupCreationPanel();
        setContentPane(panel.getJPanel());
    }

    public void onCreation(Consumer<String> onCreation)
    {
        panel.onCreation(groupName -> {
            onCreation.accept(groupName);
            close();
        });
    }


}
