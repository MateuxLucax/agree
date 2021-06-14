package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class AppFrame extends JFrame {

    private JPanel mainPanel;
    private JPanel sidePanel;
    private JPanel sideBar;

    public AppFrame(String title) {
        super(title);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        sideBar = new JPanel(new BorderLayout());

        setLayout(new BorderLayout());
        add(sideBar, BorderLayout.LINE_START);
    }

    public void addAboveSidePanel(JComponent comp) {
        sideBar.add(comp, BorderLayout.PAGE_START);
    }

    public void addBelowSidePanel(JComponent comp) {
        sideBar.add(comp, BorderLayout.PAGE_END);
    }

    public void setSidePanel(JPanel newSidePanel) {
        if (sidePanel != null) sideBar.remove(sidePanel);
        sideBar.add(newSidePanel, BorderLayout.CENTER);
        sidePanel = newSidePanel;
    }

    public void setMainPanel(JPanel newMainPanel) {
        if (mainPanel != null) remove(mainPanel);
        add(newMainPanel, BorderLayout.CENTER);
        mainPanel = newMainPanel;
    }

}
