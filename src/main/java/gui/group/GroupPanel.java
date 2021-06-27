package gui.group;

import javax.swing.*;

public class GroupPanel extends JTabbedPane {

    public void setMessagingTab(MessagingPanel msgPanel) {
        addTab("Messages", msgPanel);
    }

    public void setManagementTab(GroupManagementPanel managPanel) {
        addTab("Manage", managPanel.getJPanel());
    }
}
