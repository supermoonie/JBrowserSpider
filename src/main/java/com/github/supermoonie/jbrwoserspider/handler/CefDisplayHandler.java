package com.github.supermoonie.jbrwoserspider.handler;

import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.browser.JCefBrowser;
import com.github.supermoonie.jbrwoserspider.browser.JCefClient;
import com.github.supermoonie.jbrwoserspider.setting.UrlSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.swing.*;

/**
 * @author super_w
 * @since 2021/7/12
 */
public class CefDisplayHandler extends CefDisplayHandlerAdapter {

    @Override
    public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
        if (url.contains("devtools") || url.equals(UrlSettings.HOME)) {
            return;
        }
        JCefBrowser currentBrowser = JCefClient.getInstance().getCurrentBrowser();
        if (!browser.equals(currentBrowser.getCefBrowser())) {
            return;
        }
        currentBrowser.getControlPanel().setAddress(currentBrowser.getCefBrowser(), url);
    }

    @Override
    public void onTitleChange(CefBrowser browser, String title) {
        if (browser.getURL().equals(UrlSettings.HOME)) {
            return;
        }
        JTabbedPane tabbedPane = App.getInstance().getMainFrame().getTabbedPane();
        int selectedIndex = tabbedPane.getSelectedIndex();
        JCefBrowser currentBrowser = JCefClient.getInstance().getCurrentBrowser();
        if (!browser.equals(currentBrowser.getCefBrowser())) {
            return;
        }
        App.getInstance().getMainFrame().setTitle(title);
        tabbedPane.setTitleAt(selectedIndex, title.length() > 12 ? title.substring(0, 12) + "..." : title);
        tabbedPane.setToolTipTextAt(selectedIndex, title);
    }

    @Override
    public void onStatusMessage(CefBrowser browser, String value) {
        JCefBrowser jCefBrowser = JCefClient.getInstance().getBrowser(browser);
        if (null == jCefBrowser) {
            return;
        }
        if (null == jCefBrowser.getStatusPanel()) {
            return;
        }
        jCefBrowser.getStatusPanel().setStatusText(value);
    }
}
