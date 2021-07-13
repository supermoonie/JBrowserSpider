package com.github.supermoonie.jbrwoserspider.handler;

import com.github.supermoonie.jbrwoserspider.browser.JCefBrowser;
import com.github.supermoonie.jbrwoserspider.browser.JCefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandlerAdapter;

/**
 * @author super_w
 * @since 2021/7/13
 */
public class BrowserLoadHandler extends CefLoadHandlerAdapter {

    @Override
    public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
        JCefBrowser jCefBrowser = JCefClient.getInstance().getBrowser(browser);
        if (null == jCefBrowser) {
            return;
        }
        if (null == jCefBrowser.getControlPanel()) {
            return;
        }
        jCefBrowser.getControlPanel().update(browser, isLoading, canGoBack, canGoForward);
    }
}
