package gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class UserSearchFrame extends PopUpFrame
{
    private JPanel     mainPanel;
    private JButton    btSearch;
    private JTextField tfSearch;
    private JPanel     resultsPanel;

    // TODO "load more" button, because we won't load *all* the users that match the search, which would possibly be too much

    public UserSearchFrame(JButton btnThatOpenedTheFrame)
    {
        super(btnThatOpenedTheFrame);

        tfSearch = new JTextField();
        btSearch = new JButton("Search");

        var formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        formPanel.add(tfSearch, BorderLayout.CENTER);
        formPanel.add(btSearch, BorderLayout.LINE_END);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.PAGE_AXIS));

        var resultsScrollPane = new JScrollPane();
        resultsScrollPane.setViewportView(resultsPanel);
        resultsScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(formPanel, BorderLayout.PAGE_START);
        mainPanel.add(resultsScrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    public void addUserBar(UserBar bar)
    {
        resultsPanel.add(bar);
    }

    public void clearResults()
    {
        resultsPanel.removeAll();
    }

    public void onSearch(Consumer<String> action)
    {
        btSearch.addActionListener(e -> action.accept(tfSearch.getText()));
    }
}
