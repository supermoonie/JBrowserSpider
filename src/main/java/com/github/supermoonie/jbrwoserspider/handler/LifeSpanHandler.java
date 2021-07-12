package com.github.supermoonie.jbrwoserspider.handler;

import com.github.supermoonie.jbrwoserspider.browser.JCefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLifeSpanHandlerAdapter;

import javax.swing.*;

/**
 * @author super_w
 * @since 2021/6/16
 */
public class LifeSpanHandler extends CefLifeSpanHandlerAdapter {

    @Override
    public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String targetUrl, String targetFrameName) {
        SwingUtilities.invokeLater(() -> JCefClient.getInstance().createBrowser(targetUrl, true, true));
        return true;
    }
}
