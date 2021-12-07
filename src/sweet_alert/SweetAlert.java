package sweet_alert;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.Interpolator;

public class SweetAlert extends JDialog {

    private Background content;
    private Animator animator;
    private Dimension originalSize;
    private Point originalLocation;
    private boolean show;

    public SweetAlert(Frame fram, boolean modal) {
        super(fram, modal);
        init();
    }

    private void init() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        content = new Background();
        content.setBackground(Color.WHITE);
        setContentPane(content);
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void end() {
                if (show == false) {
                    //  Close dialog
                    dispose();
                }
            }

            @Override
            public void timingEvent(float fraction) {
                float f;
                if (show) {
                    f = fraction;
                } else {
                    f = 1f - fraction;
                }
                int w = (int) (originalSize.width * f);
                int h = (int) (originalSize.height * f);
                int x = originalLocation.x - w / 2;
                int y = originalLocation.y - h / 2;
                setLocation(x, y);
                setSize(new Dimension(w, h));
            }
        };
        animator = new Animator(500, target);
        animator.setInterpolator(new Interpolator() {
            @Override
            public float interpolate(float f) {
                if (show) {
                    return easeOutBounce(f);
                } else {
                    return easeOutExpo(f);
                }
            }
        });
        animator.setResolution(0);
        //  Add Event close dialog
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeAlert();
            }
        });
    }

    private float easeOutBounce(float x) {
        float n1 = 7.5625f;
        float d1 = 2.75f;
        double v;
        if (x < 1 / d1) {
            v = n1 * x * x;
        } else if (x < 2 / d1) {
            v = n1 * (x -= 1.5 / d1) * x + 0.75;
        } else if (x < 2.5 / d1) {
            v = n1 * (x -= 2.25 / d1) * x + 0.9375;
        } else {
            v = n1 * (x -= 2.625 / d1) * x + 0.984375;
        }
        return (float) v;
    }

    private float easeOutExpo(float x) {
        return (float) (x == 1 ? 1 : 1 - Math.pow(2, -10 * x));
    }

    public void showAlert() {
        //  For disable Alt+F4 to close dialog
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        originalSize = getPreferredSize();
        originalLocation = getLocation(getParent());
        setSize(new Dimension(0, 0));
        if (animator.isRunning()) {
            animator.stop();
        }
        show = true;
        animator.setDuration(500);
        animator.start();
        setVisible(true);
    }

    public void closeAlert() {
        if (animator.isRunning()) {
            animator.stop();
        }
        show = false;
        animator.setDuration(400);
        animator.start();
    }

    //  Get location center of parent screen
    public Point getLocation(Container parent) {
        Point location = parent.getLocationOnScreen();
        Dimension size = parent.getSize();
        int x = location.x + size.width / 2;
        int y = location.y + size.height / 2;
        Point point = new Point(x, y);
        return point;
    }

    private class Background extends JComponent {

        @Override
        public void paint(Graphics grphcs) {
            Graphics2D g2 = (Graphics2D) grphcs.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paint(grphcs);
        }
    }
}
