package gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Consumer;

public class UserSearchPanel extends JPanel {
    private JButton    btSearch;
    private JTextField tfSearch;
    private JPanel     resultsPanel;

    // TODO "load more" button, because we won't load *all* the users that match the search,
    //    which would possibly be too much
    //    I guess we'll need a UserSearchModel that takes care of dynamically loading the users then

    public UserSearchPanel() {
        tfSearch = new JTextField();
        btSearch = new JButton("Search");

        var formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        formPanel.add(tfSearch, BorderLayout.CENTER);
        formPanel.add(btSearch, BorderLayout.LINE_END);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.PAGE_AXIS));

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.PAGE_START);
        add(resultsPanel, BorderLayout.CENTER);
    }

    public void onSearch(Consumer<String> callback) {
        btSearch.addActionListener(evt -> {
            callback.accept(tfSearch.getText());
            tfSearch.setText("");
        });
    }

    public void loadResults(List<UserBar> bars) {
        resultsPanel.removeAll();
        bars.forEach(resultsPanel::add);
        resultsPanel.repaint();
        resultsPanel.revalidate();
    }
}
