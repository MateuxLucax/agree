package gui.group;

import gui.UserBar;

import javax.swing.*;

public class MemberBar extends UserBar {

    private JButton btRemove;
    private JButton btSetOwner;

    public MemberBar(String memberName) {
        super(memberName);
    }

    public void showThatIsOwner() {
        JButton btOwner = new JButton("Owner");
        buttonsPanel.add(btOwner);
        btOwner.setEnabled(false);
    }

    public void addRemoveButton(Runnable action) {
        btRemove = new JButton("Remove");
        buttonsPanel.add(btRemove);
        btRemove.addActionListener(e -> action.run());
    }

    public void addSetOwnerButton(Runnable action) {
        btSetOwner = new JButton("Set owner");
        buttonsPanel.add(btSetOwner);
        btSetOwner.addActionListener(e -> action.run());
    }
}
