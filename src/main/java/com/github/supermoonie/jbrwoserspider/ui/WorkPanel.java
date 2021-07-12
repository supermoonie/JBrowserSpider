package com.github.supermoonie.jbrwoserspider.ui;

import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;

/**
 * @author super_w
 * @since 2021/6/23
 */
public class WorkPanel extends JPanel {

    private final CefBrowser browser;

    public WorkPanel(CefBrowser browser) {
        this.browser = browser;
        setEnabled(true);
        setLayout(new BorderLayout());
        add(browser.getUIComponent(), BorderLayout.CENTER);
        setVisible(true);
    }
}
