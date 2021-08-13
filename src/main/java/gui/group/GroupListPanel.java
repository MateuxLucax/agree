package gui.group;

import javax.swing.*;
import java.awt.*;

public class GroupListPanel extends JPanel {

    private final JButton btNewGroup;
    private final JButton btRefresh;

    private final JPanel main;

    // TODO somehow call pack() on the frame after a group is added/deleted
    //   probably via a callback

    public GroupListPanel() {
        setLayout(new BorderLayout());

        btNewGroup = new JButton("+ New group");
        btRefresh = new JButton("Refresh");

        var header = new JPanel();
        header.add(btNewGroup);
        header.add(btRefresh);

        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));

        var scrollPane = new JScrollPane();
        scrollPane.setViewportView(main);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        add(header, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addGroupBar(GroupBar bar) {
        main.add(bar);
        main.repaint();
        main.revalidate();
    }

    public void removeGroupBar(GroupBar bar) {
        main.remove(bar);
        main.repaint();
        main.revalidate();
    }

    public void clear() {
        main.removeAll();
        main.repaint();
        main.revalidate();
    }

    public void newGroupButtonSetEnabled(boolean b) {
        btNewGroup.setEnabled(b);
    }

    public void onClickNewGroup(Runnable action) {
        btNewGroup.addActionListener(e -> action.run());
    }

    public void onClickRefresh(Runnable action) {
        btRefresh.addActionListener(e -> action.run());
    }
}
