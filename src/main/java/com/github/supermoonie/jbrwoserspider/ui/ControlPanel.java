package com.github.supermoonie.jbrwoserspider.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.cef.OS;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author super_w
 * @since 2021/6/15
 */
public class ControlPanel extends JPanel {

    private static final Icon LEFT_ICON = new FlatSVGIcon("icons/left.svg", 16, 16);
    private static final Icon RIGHT_ICON = new FlatSVGIcon("icons/right.svg", 16, 16);
    private static final Icon REFRESH_ICON = new FlatSVGIcon("icons/refresh.svg", 16, 16);
    private static final Icon CLOSE_ICON = new FlatSVGIcon("icons/close.svg", 16, 16);

    private final JButton backButton;
    private final JButton forwardButton;
    private final JButton reloadButton;
    private final JTextField addressField;
    private final CefBrowser cefBrowser;

    public ControlPanel(CefBrowser cefBrowser) {
        this.cefBrowser = cefBrowser;
        setEnabled(true);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalStrut(5));
        add(Box.createHorizontalStrut(5));

        backButton = new JButton(LEFT_ICON);
        backButton.setAlignmentX(LEFT_ALIGNMENT);
        backButton.addActionListener(e -> cefBrowser.goBack());
        add(backButton);
        add(Box.createHorizontalStrut(5));

        forwardButton = new JButton(RIGHT_ICON);
        forwardButton.setAlignmentX(LEFT_ALIGNMENT);
        forwardButton.addActionListener(e -> cefBrowser.goForward());
        add(forwardButton);
        add(Box.createHorizontalStrut(5));

        reloadButton = new JButton(REFRESH_ICON);
        reloadButton.setAlignmentX(LEFT_ALIGNMENT);
        reloadButton.addActionListener(e -> {
            if (reloadButton.getIcon().equals(REFRESH_ICON)) {
                int mask = OS.isMacintosh()
                        ? ActionEvent.META_MASK
                        : ActionEvent.CTRL_MASK;
                if ((e.getModifiers() & mask) != 0) {
                    cefBrowser.reloadIgnoreCache();
                } else {
                    cefBrowser.reload();
                }
            } else {
                cefBrowser.stopLoad();
            }
        });
        add(reloadButton);
        add(Box.createHorizontalStrut(5));

        addressField = new JTextField(100);
        addressField.setAlignmentX(LEFT_ALIGNMENT);
        addressField.addActionListener(e -> cefBrowser.loadURL(getAddress()));
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

        setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2));
    }

    public void update(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
        if (browser == this.cefBrowser) {
            backButton.setEnabled(canGoBack);
            forwardButton.setEnabled(canGoForward);
            reloadButton.setIcon(isLoading ? CLOSE_ICON : REFRESH_ICON);
        }
    }

    public String getAddress() {
        String address = addressField.getText();
        address = address.replaceAll(" ", "%20");
        return address;
    }

    public void setAddress(CefBrowser browser, String address) {
        if (browser == this.cefBrowser) {
            addressField.setText(address);
        }
    }

    public void setCefBrowser(CefBrowser cefBrowser) {
        backButton.setEnabled(cefBrowser.canGoBack());
        forwardButton.setEnabled(cefBrowser.canGoForward());
        reloadButton.setIcon(cefBrowser.isLoading() ? CLOSE_ICON : REFRESH_ICON);
        addressField.setText(cefBrowser.getURL());
    }
}
