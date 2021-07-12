package com.github.supermoonie.jbrwoserspider.ui;

import com.github.supermoonie.jbrwoserspider.browser.JCefBrowser;
import com.github.supermoonie.jbrwoserspider.setting.UrlSettings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * @author super_w
 * @since 2021/7/11
 */
@Slf4j
public class HomeTab extends JPanel {

    @Getter
    private final JCefBrowser jCefBrowser;

    public HomeTab() {
        setEnabled(true);
        setLayout(new BorderLayout());
        log.info("load {}", UrlSettings.HOME);
        jCefBrowser = new JCefBrowser(UrlSettings.HOME, true, false);
        add(jCefBrowser, BorderLayout.CENTER);
        setVisible(true);
    }
}
