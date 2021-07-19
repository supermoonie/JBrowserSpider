package com.github.supermoonie.jbrwoserspider.browser;

import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.dialog.DevToolsDialog;
import com.github.supermoonie.jbrwoserspider.ui.ControlPanel;
import com.github.supermoonie.jbrwoserspider.ui.StatusPanel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;

/**
 * @author super_w
 * @since 2021/7/11
 */
@Slf4j
@Getter
public class JCefBrowser extends JPanel {

    private final boolean showControl;
    private final boolean showSpider;
    private final String currentUrl;
    private final CefBrowser cefBrowser;
    private CefBrowser workCefBrowser;
    private ControlPanel controlPanel;
    private final StatusPanel statusPanel;
    private DevToolsDialog devTools;
    private DevToolsDialog workDevTools;
    private JPanel browserPanel;
    private JPanel workBrowserPanel;
    private JSplitPane splitPane;
    @Setter
    private String title;

    public JCefBrowser(String currentUrl, boolean showControl, boolean showSpider, String spiderHomeUrl) {
        this.currentUrl = currentUrl;
        this.showControl = showControl;
        this.showSpider = showSpider;
        this.cefBrowser = JCefClient.getInstance().getDefaultCefClient().createBrowser(this.currentUrl, false, false);
        setLayout(new BorderLayout());
        if (showSpider && StringUtils.isNotEmpty(spiderHomeUrl)) {
            splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerSize(20);
            browserPanel = new JPanel(new BorderLayout());
            if (this.showControl) {
                controlPanel = new ControlPanel(this.cefBrowser);
                browserPanel.add(this.controlPanel, BorderLayout.NORTH);
            }
            browserPanel.add(this.cefBrowser.getUIComponent(), BorderLayout.CENTER);
            statusPanel = new StatusPanel();
            browserPanel.add(this.statusPanel, BorderLayout.SOUTH);
            splitPane.setTopComponent(browserPanel);
            workBrowserPanel = new JPanel(new BorderLayout());
            this.workCefBrowser = JCefClient.getInstance().getDefaultCefClient().createBrowser(spiderHomeUrl, false, false);
            workBrowserPanel.add(this.workCefBrowser.getUIComponent(), BorderLayout.CENTER);
            splitPane.setBottomComponent(workBrowserPanel);
            add(splitPane, BorderLayout.CENTER);
        } else {
            if (this.showControl) {
                controlPanel = new ControlPanel(this.cefBrowser);
                add(this.controlPanel, BorderLayout.NORTH);
            }
            add(this.cefBrowser.getUIComponent(), BorderLayout.CENTER);
            statusPanel = new StatusPanel();
            add(this.statusPanel, BorderLayout.SOUTH);
        }

        setEnabled(true);
        setVisible(true);
        SwingUtilities.invokeLater(() -> {
            if (null != splitPane) {
                splitPane.setDividerLocation(0.8);
            }
        });
    }

    public void showDevTools() {
        devTools = new DevToolsDialog(App.getInstance().getMainFrame(), "DEV Tools", cefBrowser);
        devTools.setVisible(true);
    }

    public void showWorkDevTools() {
        if (null != workCefBrowser) {
            this.workDevTools = new DevToolsDialog(App.getInstance().getMainFrame(), "DEV Tools", workCefBrowser);
            this.workDevTools.setVisible(true);
        }
    }

}
