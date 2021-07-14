package com.github.supermoonie.jbrwoserspider.handler;

import com.github.supermoonie.jbrwoserspider.browser.JCefClient;
import com.github.supermoonie.jbrwoserspider.setting.UrlSettings;
import lombok.extern.slf4j.Slf4j;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLifeSpanHandlerAdapter;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author super_w
 * @since 2021/6/16
 */
@Slf4j
public class LifeSpanHandler extends CefLifeSpanHandlerAdapter {

    @Override
    public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String targetUrl, String targetFrameName) {
        SwingUtilities.invokeLater(() -> {
            try {
                URL url = new URL(targetUrl);
                String host = url.getHost();
                if (UrlSettings.HOST_HOME_MAP.containsKey(host)) {
                    JCefClient.getInstance().createBrowser(targetUrl, true, true, UrlSettings.HOST_HOME_MAP.get(host));
                } else {
                    JCefClient.getInstance().createBrowser(targetUrl, true, false, null);
                }
            } catch (MalformedURLException e) {
                log.error(e.getMessage(), e);
                JCefClient.getInstance().createBrowser(targetUrl, true, true, null);
            }
        });
        return true;
    }
}
