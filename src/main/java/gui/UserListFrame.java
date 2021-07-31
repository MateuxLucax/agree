package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserListFrame extends JFrame {

    private final JPanel barsPanel;
    private Runnable onClose;

    public UserListFrame()
    {
        var scrollPane = new JScrollPane();
        barsPanel = new JPanel();
        barsPanel.setLayout(new BoxLayout(barsPanel, BoxLayout.PAGE_AXIS));
        scrollPane.setViewportView(barsPanel);
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

    public void onClose(Runnable action)
    {
        onClose = action;
    }

    public void addUserBar(UserBar bar) {
        barsPanel.add(bar);
    }

    public void removeUserBar(UserBar bar) {
        barsPanel.remove(bar);
    }
}
