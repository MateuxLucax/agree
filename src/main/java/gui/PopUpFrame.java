package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// TODO delete this class, it's not doing much anymore

public class PopUpFrame extends JFrame
{
    private Runnable onClose;

    public PopUpFrame()
    {
        super();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                close();
            }
        });
    }

    public void onClose(Runnable action)
    {
        this.onClose = action;
    }

    public void close() {
        if (onClose != null)
            onClose.run();
        dispose();
    }

}
