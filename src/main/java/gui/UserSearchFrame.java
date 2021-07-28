package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class UserSearchFrame extends PopUpFrame
{
    private JPanel     mainPanel;
    private JButton    btSearch;
    private JTextField tfSearch;
    private JPanel     resultsPanel;

    // TODO "load more" button, because we won't load *all* the users that match the search,
    //    which would possibly be too much
    //    I guess we'll need a UserSearchModel that takes care of dynamically loading the users then

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

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(formPanel, BorderLayout.PAGE_START);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    public void onSearch(Function<String, List<UserBar>> searchFn)
    {
        btSearch.addActionListener(evt -> {
            loadResults(searchFn.apply(tfSearch.getText()));
            tfSearch.setText("");
        });
    }

    public void loadResults(List<UserBar> bars)
    {
        resultsPanel.removeAll();
        bars.forEach(resultsPanel::add);
        resultsPanel.repaint();
        resultsPanel.revalidate();
    }
}
