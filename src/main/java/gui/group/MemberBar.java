package gui.group;

import gui.UserBar;

import javax.swing.*;

public class MemberBar extends UserBar {

    private JButton btRemove;
    private JButton btSetOwner;
    private final String memberName;

    public MemberBar(String memberName) {
        this.memberName = memberName;
        super(memberName);
    }

    public void showThatIsOwner()
    {
        JButton btOwner = new JButton("Owner");
        buttonsPanel.add(btOwner);
        btOwner.setEnabled(false);
    }

    public void addRemoveButton(Runnable action)
    {
        btRemove = new JButton("Remove");
        buttonsPanel.add(btRemove);
        btRemove.addActionListener(e -> action.run());
    }

    public void addSetOwnerButton(Runnable action)
    {
        btSetOwner = new JButton("Set owner");
        buttonsPanel.add(btSetOwner);
        btSetOwner.addActionListener(e -> action.run());
    }

    public boolean confirmRemove(String memberName)
    {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to remove " + memberName + " from the group?",
                "Remove member",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public boolean confirmSetOwner(String memberName)
    {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to set " + memberName + " as the new owner?",
                "Set owner",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public void warnCouldNotRemove()
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not remove " + memberName + " from the group",
                "Remove member",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void warnCouldNotSetOwner()
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not set " + memberName + " as the group owner",
                "Remove member",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
