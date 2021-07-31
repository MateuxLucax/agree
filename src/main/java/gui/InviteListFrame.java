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
        var invitesScrollPane = new JScrollPane();
        invitesPanel = new JPanel();
        invitesPanel.setLayout(new BoxLayout(invitesPanel, BoxLayout.PAGE_AXIS));
        invitesScrollPane.setViewportView(invitesPanel);
        invitesScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(invitesScrollPane);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (onClose != null)
                    onClose.run();
                dispose();
            }
        });
    }

    public void addInvite(InviteBar bar)
    {
        invitesPanel.add(bar);
    }

    public void onClose(Runnable action)
    {
        onClose = action;

    }
}
