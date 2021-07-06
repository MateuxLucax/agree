package gui;

import javax.swing.*;

public class InviteListPanel extends JScrollPane {
    private JPanel invitesPanel;
    public InviteListPanel() {
        invitesPanel = new JPanel();
        invitesPanel.setLayout(new BoxLayout(invitesPanel, BoxLayout.PAGE_AXIS));
        setViewportView(invitesPanel);
        getVerticalScrollBar().setUnitIncrement(20);
    }
    public void addInvite(InvitesBar bar) {
        invitesPanel.add(bar);
    }
}
