package com.github.supermoonie.jbrwoserspider.ui;

import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.setting.UrlSettings;
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
public class HomeTab extends JPanel {

    @Getter
    private final CefBrowser homeBrowser;

    public HomeTab() {
        setEnabled(true);
        setLayout(new BorderLayout());
        log.info("load {}", UrlSettings.HOME);
        homeBrowser = App.getInstance().getDefaultCefClient().createBrowser(UrlSettings.HOME, false, false);
        add(homeBrowser.getUIComponent(), BorderLayout.CENTER);
        setVisible(true);
    }
}
