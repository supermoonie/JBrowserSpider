package com.github.supermoonie.jbrwoserspider.browser;

import com.github.supermoonie.jbrwoserspider.ui.ControlPanel;
import lombok.Getter;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;

/**
 * @author super_w
 * @since 2021/7/11
 */
@Getter
public class JCefBrowser extends JPanel {

    private final boolean showControl;
    private final boolean showSpider;
    private final String currentUrl;
    private final CefBrowser cefBrowser;
    private ControlPanel controlPanel;

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
        setVisible(true);
    }

}
