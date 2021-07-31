package gui;

import javax.swing.*;

public class MorePanel extends JPanel
{
    private final JButton btSearch;
    private final JButton btInvites;
    private final JButton btUsg;
    // USG = [U]sers in the [S]ame [G]roups as you
    // to avoid long variable/method names

    public MorePanel()
    {
        btSearch = new JButton("Search for users");
        add(btSearch);

        btInvites = new JButton("Invites");
        add(btInvites);

        btUsg = new JButton("Users in the same groups as you");
        add(btUsg);
    }

    public void onClickSearchButton(Runnable action)
    {
        btSearch.addActionListener(e -> action.run());
    }

    public void onClickInvitesButton(Runnable action)
    {
        btInvites.addActionListener(e -> action.run());
    }

    public void onClickUsgButton(Runnable action)
    {
        btUsg.addActionListener(e -> action.run());
    }

    public void searchButtonSetEnabled(boolean b)
    {
        btSearch.setEnabled(b);
    }

    public void invitesButtonSetEnabled(boolean b)
    {
        btInvites.setEnabled(b);
    }

    public void usgButtonSetEnabled(boolean b)
    {
        btUsg.setEnabled(b);
    }
}
