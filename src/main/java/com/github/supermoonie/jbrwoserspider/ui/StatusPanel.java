package com.github.supermoonie.jbrwoserspider.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author super_w
 * @since 2021/6/15
 */
public class StatusPanel extends JPanel {

    private final JProgressBar progressBar;
    private final JLabel statusField;

    public StatusPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalStrut(5));
        add(Box.createHorizontalStrut(5));

        progressBar = new JProgressBar();
        Dimension progressBarSize = progressBar.getMaximumSize();
        progressBarSize.width = 100;
        progressBar.setMinimumSize(progressBarSize);
        progressBar.setMaximumSize(progressBarSize);
        add(progressBar);
        add(Box.createHorizontalStrut(5));

        statusField = new JLabel("Info");
        statusField.setAlignmentX(LEFT_ALIGNMENT);
        add(statusField);
        add(Box.createHorizontalStrut(5));
        add(Box.createVerticalStrut(21));
    }

    public void setIsInProgress(boolean inProgress) {
        progressBar.setIndeterminate(inProgress);
    }

    public void setStatusText(String text) {
        statusField.setText(text);
    }
}
