package gui;

import javax.swing.*;

public class UserListPanel extends JScrollPane {

    private final JPanel barsPanel;

    public UserListPanel() {
        barsPanel = new JPanel();
        barsPanel.setLayout(new BoxLayout(barsPanel, BoxLayout.PAGE_AXIS));
        setViewportView(barsPanel);
        getVerticalScrollBar().setUnitIncrement(20);
    }

    public void addUserBar(UserBar bar) {
        barsPanel.add(bar);
    }

    public void removeUserBar(UserBar bar) {
        barsPanel.remove(bar);
    }
}
