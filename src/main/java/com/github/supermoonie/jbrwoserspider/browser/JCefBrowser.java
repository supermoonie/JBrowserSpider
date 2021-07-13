package com.github.supermoonie.jbrwoserspider.browser;

import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.dialog.DevToolsDialog;
import com.github.supermoonie.jbrwoserspider.ui.ControlPanel;
import com.github.supermoonie.jbrwoserspider.ui.StatusPanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
    private ControlPanel controlPanel;
    private final StatusPanel statusPanel;
    private final DevToolsDialog devTools;

    public JCefBrowser(String currentUrl, boolean showControl, boolean showSpider) {
        this.currentUrl = currentUrl;
        this.showControl = showControl;
        this.showSpider = showSpider;
        this.cefBrowser = JCefClient.getInstance().getDefaultCefClient().createBrowser(this.currentUrl, false, false);
        setLayout(new BorderLayout());
        if (this.showControl) {
            controlPanel = new ControlPanel(this.cefBrowser);
            add(this.controlPanel, BorderLayout.NORTH);
        }
        add(this.cefBrowser.getUIComponent(), BorderLayout.CENTER);
        statusPanel = new StatusPanel();
        add(this.statusPanel, BorderLayout.SOUTH);
        devTools = new DevToolsDialog(App.getInstance().getMainFrame(), "DEV Tools", cefBrowser);
        setEnabled(true);
        setVisible(true);
    }

}
