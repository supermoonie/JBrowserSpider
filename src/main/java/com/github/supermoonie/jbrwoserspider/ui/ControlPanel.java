package com.github.supermoonie.jbrwoserspider.ui;

import org.cef.OS;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author super_w
 * @since 2021/6/15
 */
public class ControlPanel extends JPanel {
    private final JButton backButton;
    private final JButton forwardButton;
    private final JButton reloadButton;
    private final JTextField addressField;
    private final JLabel zoomLabel;
    private double zoomLevel = 0;
    private CefBrowser browser;

    public ControlPanel() {
        setEnabled(true);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalStrut(5));
        add(Box.createHorizontalStrut(5));

        backButton = new JButton("Back");
        backButton.setAlignmentX(LEFT_ALIGNMENT);
        backButton.addActionListener(e -> browser.goBack());
        add(backButton);
        add(Box.createHorizontalStrut(5));

        forwardButton = new JButton("Forward");
        forwardButton.setAlignmentX(LEFT_ALIGNMENT);
        forwardButton.addActionListener(e -> browser.goForward());
        add(forwardButton);
        add(Box.createHorizontalStrut(5));

        reloadButton = new JButton("Reload");
        reloadButton.setAlignmentX(LEFT_ALIGNMENT);
        reloadButton.addActionListener(e -> {
            if (reloadButton.getText().equalsIgnoreCase("reload")) {
                int mask = OS.isMacintosh()
                        ? ActionEvent.META_MASK
                        : ActionEvent.CTRL_MASK;
                if ((e.getModifiers() & mask) != 0) {
                    System.out.println("Reloading - ignoring cached values");
                    browser.reloadIgnoreCache();
                } else {
                    System.out.println("Reloading - using cached values");
                    browser.reload();
                }
            } else {
                browser.stopLoad();
            }
        });
        add(reloadButton);
        add(Box.createHorizontalStrut(5));

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(addressLabel);
        add(Box.createHorizontalStrut(5));

        addressField = new JTextField(100);
        addressField.setAlignmentX(LEFT_ALIGNMENT);
        addressField.addActionListener(e -> browser.loadURL(getAddress()));
        addressField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager()
                        .clearGlobalFocusOwner();
                addressField.requestFocus();
            }
        });
        add(addressField);
        add(Box.createHorizontalStrut(5));

        JButton goButton = new JButton("Go");
        goButton.setAlignmentX(LEFT_ALIGNMENT);
        goButton.addActionListener(e -> browser.loadURL(getAddress()));
        add(goButton);
        add(Box.createHorizontalStrut(5));

        JButton minusButton = new JButton("-");
        minusButton.setAlignmentX(CENTER_ALIGNMENT);
        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browser.setZoomLevel(--zoomLevel);
                zoomLabel.setText(Double.toString(zoomLevel));
            }
        });
        add(minusButton);

        zoomLabel = new JLabel("0.0");
        zoomLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        add(zoomLabel);

        JButton plusButton = new JButton("+");
        plusButton.setAlignmentX(CENTER_ALIGNMENT);
        plusButton.addActionListener(e -> {
            browser.setZoomLevel(++zoomLevel);
            zoomLabel.setText(Double.toString(zoomLevel));
        });
        add(plusButton);
        setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2));
    }

    public void update(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
        if (browser == this.browser) {
            backButton.setEnabled(canGoBack);
            forwardButton.setEnabled(canGoForward);
            reloadButton.setText(isLoading ? "Abort" : "Reload");
        }
    }

    public String getAddress() {
        String address = addressField.getText();
        address = address.replaceAll(" ", "%20");
        return address;
    }

    public void setAddress(CefBrowser browser, String address) {
        if (browser == this.browser)
            addressField.setText(address);
    }

    public void setBrowser(CefBrowser browser) {
        this.browser = browser;
        backButton.setEnabled(browser.canGoBack());
        forwardButton.setEnabled(browser.canGoForward());
        reloadButton.setText(browser.isLoading() ? "Abort" : "Reload");
        addressField.setText(browser.getURL());
    }
}
