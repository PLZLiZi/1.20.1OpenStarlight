package net.daichang.starlight.client.window.java.starbutton;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

public class StarJButton extends JButton {
    Color color = Color.GRAY;

    public StarJButton(String text, String tooltip) {
        setBackground(color);
        setText(text);
        setToolTipText(tooltip);
        setBorder(new RoundedBorder(20, Color.BLACK));
    }

    public StarJButton(String text) {
        setBackground(color);
        setText(text);
        setBorder(new RoundedBorder(20, Color.BLACK));
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius+2);
        }

        @Override
        public boolean isBorderOpaque() {
            return super.isBorderOpaque();
        }
    }
}
