package bank.gui;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {

    public WelcomePanel(MainWindow mainWindow) {
        initUI(mainWindow);
    }

    private void initUI(MainWindow mainWindow) {
        Theme.stylePanel(this);
        setLayout(new GridBagLayout());

        JLabel title = new JLabel("Welcome to the Bank Transaction Monitoring System", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Developed as part of the Internal Assessment for Java Programming and Database Management Systems (Semester IV \u2013 Diploma in Computer Engineering).", SwingConstants.CENTER);
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);

        JLabel credits = new JLabel("Developed by: Abhiram Dighe (244011) & Pracheta Satapathy (244012)", SwingConstants.CENTER);
        credits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        credits.setForeground(Theme.TEXT_SECONDARY);

        Theme.RoundedButton continueButton = new Theme.RoundedButton("Continue to Login");
        continueButton.setPreferredSize(new Dimension(220, 40));
        continueButton.addActionListener(e -> mainWindow.showScreen(MainWindow.SCREEN_LOGIN));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 24, 10, 24);
        g.gridx = 0; g.gridy = 0; g.fill = GridBagConstraints.HORIZONTAL;
        add(title, g);

        g.gridy = 1; g.insets = new Insets(10, 80, 10, 80);
        add(subtitle, g);

        g.gridy = 2; g.insets = new Insets(10, 80, 10, 80);
        add(credits, g);

        g.gridy = 3; g.insets = new Insets(24, 0, 24, 0); g.anchor = GridBagConstraints.CENTER;
        add(continueButton, g);
    }
}
