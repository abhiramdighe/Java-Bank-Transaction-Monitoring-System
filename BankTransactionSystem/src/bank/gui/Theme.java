package bank.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Theme {
    public static final Color BG_PRIMARY = new Color(0x12, 0x12, 0x12);
    public static final Color BG_SECONDARY = new Color(0x1E, 0x1E, 0x1E);
    public static final Color BG_CARD = new Color(0x2A, 0x2A, 0x2A);
    public static final Color ACCENT = new Color(0x00, 0xA8, 0x6B);
    public static final Color TEXT_PRIMARY = new Color(0xFF, 0xFF, 0xFF);
    public static final Color TEXT_SECONDARY = new Color(0xB0, 0xB0, 0xB0);
    public static final Color BORDER = new Color(0x33, 0x33, 0x33);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.BOLD, 14);

    public static class RoundedButton extends JButton {
        private static final int ARC = 18;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(TEXT_PRIMARY);
            setBackground(ACCENT);
            setFont(FONT_BODY);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new LineBorder(BORDER, 1, true));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(ACCENT.brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(ACCENT);
                    setLocation(getX(), getY());
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    setLocation(getX(), getY() + 1);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    setLocation(getX(), getY() - 1);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
            super.paintComponent(g2d);
            g2d.dispose();
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg != null ? bg : ACCENT);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(BORDER);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
            g2d.dispose();
        }
    }

    public static void styleButton(JButton button) {
        if (button == null) return;
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(ACCENT);
        button.setFont(FONT_BODY);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorder(new LineBorder(BORDER, 1, true));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setLocation(button.getX(), button.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setLocation(button.getX(), button.getY() - 1);
            }
        });
    }

    public static void stylePanel(JPanel panel) {
        if (panel == null) return;
        panel.setBackground(BG_PRIMARY);
        panel.setOpaque(true);
    }

    public static void styleLabel(JLabel label) {
        if (label == null) return;
        label.setForeground(TEXT_PRIMARY);
        label.setFont(FONT_BODY);
    }
}
