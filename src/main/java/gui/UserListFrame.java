package gui;

import javax.swing.*;

public class UserListFrame extends PopUpFrame {

    private final JPanel barsPanel;

    public UserListFrame(JButton btnThatOpenedTheFrame) {
        super(btnThatOpenedTheFrame);
        var scrollPane = new JScrollPane();
        barsPanel = new JPanel();
        barsPanel.setLayout(new BoxLayout(barsPanel, BoxLayout.PAGE_AXIS));
        scrollPane.setViewportView(barsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(scrollPane);
    }

    public void addUserBar(UserBar bar) {
        barsPanel.add(bar);
    }

    public void removeUserBar(UserBar bar) {
        barsPanel.remove(bar);
    }
}
