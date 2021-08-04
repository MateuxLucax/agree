package gui.group;

import gui.UserBar;

import javax.swing.*;

public class MemberBar extends UserBar {

    private JButton btRemove;
    private JButton btSetOwner;

    public MemberBar(String memberName) {
        super(memberName);
    }

    public void showOwnerButton() {
        JButton btOwner = new JButton("Owner");
        buttonsPanel.add(btOwner);
        btOwner.setEnabled(false);
    }

    public void showRemoveButton() {
        btRemove = new JButton("Remove");
        buttonsPanel.add(btRemove);
    }

    public void showSetOwnerButton() {
        btSetOwner = new JButton("Set owner");
        buttonsPanel.add(btSetOwner);
    }

    public void onClickRemove(Runnable action) {
        if (btRemove != null)
            btRemove.addActionListener(e -> action.run());
    }

    public void onClickSetOwner(Runnable action) {
        if (btSetOwner != null)
            btSetOwner.addActionListener(e -> action.run());
    }
}
