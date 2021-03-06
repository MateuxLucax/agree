package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UsersInSameGroupsFrame extends JFrame
{
    // TODO consider reusing the UserListFrame here, the classes are very similar,
    //   and the UserListFrame was intended to be a more generic one

    private Runnable onClose;
    private final JPanel panel;

    public UsersInSameGroupsFrame()
    {
        setTitle("Users in the same group as you");

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        var scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(scrollPane);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (onClose != null)
                    onClose.run();
                dispose();
            }
        });
    }

    // TODO not only a label, but also with an "ask to be friends" button
    //   just like in the user search frame (try to reuse the UserBar stuff)
    public void addUser(JLabel lbUser)
    {
        panel.add(lbUser);
    }

    public void onClose(Runnable action)
    {
        onClose = action;
    }

    public void display()
    {
        pack();
        setVisible(true);
    }
}
