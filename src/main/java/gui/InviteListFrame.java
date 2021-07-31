package gui;

import javax.swing.*;

public class InviteListFrame extends PopUpFrame
{
    private final JPanel invitesPanel;

    public InviteListFrame()
    {
        var invitesScrollPane = new JScrollPane();
        invitesPanel = new JPanel();
        invitesPanel.setLayout(new BoxLayout(invitesPanel, BoxLayout.PAGE_AXIS));
        invitesScrollPane.setViewportView(invitesPanel);
        invitesScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(invitesScrollPane);
    }

    public void addInvite(InviteBar bar)
    {
        invitesPanel.add(bar);
    }
}
