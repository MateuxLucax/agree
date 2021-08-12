package gui.group;

import javax.swing.*;

public class GroupListPanel extends JPanel
{
    private final JButton btNewGroup;

    public GroupListPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        btNewGroup = new JButton("+ New group");
        add(btNewGroup);
    }

    public void newGroupButtonSetEnabled(boolean b)
    {
        btNewGroup.setEnabled(b);
    }

    public void onClickNewGroup(Runnable action)
    {
        btNewGroup.addActionListener(e -> action.run());
    }
}
