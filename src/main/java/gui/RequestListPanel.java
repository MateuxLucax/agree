package gui;

import javax.swing.*;

public class RequestListPanel extends JPanel {
    public RequestListPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }
    public void addRequest(RequestBar bar) {
        add(bar);
    }
}
