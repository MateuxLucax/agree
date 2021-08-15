package gui.group;

import gui.UserBar;

import javax.swing.*;

public class MemberBar extends UserBar {

    private JButton btRemove;
    private JButton btSetOwner;

    public MemberBar(String memberName) {
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

    public boolean confirmRemove(String memberNickname)
    {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to remove " + memberNickname + " from the group?",
                "Remove member",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public boolean confirmSetOwner(String memberNickname)
    {
        return JOptionPane.showConfirmDialog(
                this,
                "Do you really want to set " + memberNickname + " as the new owner?",
                "Set owner",
                JOptionPane.YES_NO_OPTION
        ) == 0;
    }

    public void warnCouldNotRemove(String memberNickname)
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not remove " + memberNickname + " from the group",
                "Remove member",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void warnCouldNotSetOwner(String memberNickname)
    {
        JOptionPane.showMessageDialog(
                this,
                "Could not set " + memberNickname + " as the group owner",
                "Remove member",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
