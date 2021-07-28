package gui;

import javax.swing.*;

public class InviteListFrame extends PopUpFrame
{
    private JPanel invitesPanel;

    public InviteListFrame(JButton btnThatOpenedTheFrame)
    {
        super(btnThatOpenedTheFrame);
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
