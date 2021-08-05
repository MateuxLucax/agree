package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InviteListFrame extends JFrame
{
    private final JPanel invitesPanel;
    private Runnable onClose;

    public InviteListFrame()
    {
        invitesPanel = new JPanel();
        invitesPanel.setLayout(new BoxLayout(invitesPanel, BoxLayout.PAGE_AXIS));

        var scrollPane = new JScrollPane();
        scrollPane.setViewportView(invitesPanel);
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

    public void addInviteBar(InviteBar bar)
    {
        invitesPanel.add(bar);
    }

    public void removeInviteBar(InviteBar bar)
    {
        invitesPanel.remove(bar);
        invitesPanel.repaint();
        invitesPanel.revalidate();
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
