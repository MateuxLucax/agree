package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/* PopUpFrames are opened ("pop up") when the user clicks a button.
   While it is open, the button is disabled, and when it closes,
   the frame is diposed and the button becomes enabled again.

   It's basically a JFrame that comes with a window listener
   that re-enables the button and disposes the frame when
   the window closes.  A lot of frames behave like this, so it made
   sense to compress this behavior in this class at the time.
 */

public class PopUpFrame extends JFrame
{
    public PopUpFrame(JButton btnThatOpenedTheFrame)
    {
        super();
        JButton btn = btnThatOpenedTheFrame;
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                btn.setEnabled(true);
                btn.repaint();
                btn.revalidate();
                dispose();
            }
        });
    }
}
