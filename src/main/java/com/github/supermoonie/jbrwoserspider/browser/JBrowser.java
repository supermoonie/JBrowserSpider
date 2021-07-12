package com.github.supermoonie.jbrwoserspider.browser;

import lombok.Getter;

import javax.swing.*;

/**
 * @author super_w
 * @since 2021/7/11
 */
@Getter
public class JBrowser extends JPanel {

    private final boolean showMenuBar;
    private final boolean showSpiderPanel;
    private final String currentUrl;

    public JBrowser(boolean showMenuBar, boolean showSpiderPanel, String currentUrl) {
        this.showMenuBar = showMenuBar;
        this.showSpiderPanel = showSpiderPanel;
        this.currentUrl = currentUrl;
    }
}
