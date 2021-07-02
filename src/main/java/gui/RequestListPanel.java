package gui;

import javax.swing.*;

public class RequestListPanel extends JScrollPane {
    private JPanel reqsPanel;
    public RequestListPanel() {
        reqsPanel = new JPanel();
        reqsPanel.setLayout(new BoxLayout(reqsPanel, BoxLayout.PAGE_AXIS));
        setViewportView(reqsPanel);
        getVerticalScrollBar().setUnitIncrement(20);
    }
    public void addRequest(RequestBar bar) {
        reqsPanel.add(bar);
    }
}
