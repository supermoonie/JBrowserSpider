package com.github.supermoonie.jbrwoserspider.browser;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.handler.BrowserLoadHandler;
import com.github.supermoonie.jbrwoserspider.handler.CefDisplayHandler;
import com.github.supermoonie.jbrwoserspider.handler.FocusHandler;
import com.github.supermoonie.jbrwoserspider.handler.LifeSpanHandler;
import com.github.supermoonie.jbrwoserspider.setting.UrlSettings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.util.List;
import java.util.Vector;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

/**
 * @author super_w
 * @since 2021/7/12
 */
@Slf4j
public class JCefClient {

    private static JCefClient INSTANCE;

    @Getter
    private final CefClient defaultCefClient;
    private final List<JCefBrowser> browserList = new Vector<>();

    private JCefClient() {
        defaultCefClient = CefApp.getInstance().createClient();
        log.info("defaultCefClient created");
        defaultCefClient.addLifeSpanHandler(new LifeSpanHandler());
        defaultCefClient.addDisplayHandler(new CefDisplayHandler());
        defaultCefClient.addFocusHandler(new FocusHandler());
        defaultCefClient.addLoadHandler(new BrowserLoadHandler());
        JTabbedPane tabbedPane = App.getInstance().getMainFrame().getTabbedPane();
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK, (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
            if (browserList.size() == 1) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(App.getInstance().getMainFrame(), "确认退出？", "提示", JOptionPane.YES_NO_OPTION)) {
                    App.getInstance().getExecutor().shutdownNow();
                    CefApp.getInstance().dispose();
                    System.exit(0);
                }
                return;
            }
            JCefBrowser jCefBrowser = browserList.get(tabIndex);
            if (null != jCefBrowser.getWorkCefBrowser()) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(App.getInstance().getMainFrame(), "确认退出？", "提示", JOptionPane.YES_NO_OPTION)) {
                    browserList.get(tabIndex).getCefBrowser().close(true);
                    browserList.remove(tabIndex.intValue());
                    tabPane.removeTabAt(tabIndex);
                }
                return;
            }
            browserList.get(tabIndex).getCefBrowser().close(true);
            browserList.remove(tabIndex.intValue());
            tabPane.removeTabAt(tabIndex);
        });
    }

    public static JCefClient getInstance() {
        if (null == INSTANCE) {
            synchronized (JCefClient.class) {
                if (null == INSTANCE) {
                    INSTANCE = new JCefClient();
                    log.info("JCefClient initialed");
                }
            }
        }
        return INSTANCE;
    }

    public void createBrowser(String url, boolean showControl, boolean showSpider, String spiderHomeUrl) {
        JCefBrowser browser = new JCefBrowser(url, showControl, showSpider, spiderHomeUrl);
        browserList.add(browser);
        JTabbedPane tabbedPane = App.getInstance().getMainFrame().getTabbedPane();
        if (UrlSettings.HOME.equals(url)) {
            tabbedPane.addTab("Home", new FlatSVGIcon("icons/homeFolder.svg", 16, 16), browser, "主页");
        } else {
            tabbedPane.addTab(".", browser);
        }
        int lastIndex = tabbedPane.getTabCount() - 1;
        tabbedPane.setSelectedIndex(lastIndex);
    }

    public JCefBrowser getBrowser(CefBrowser cefBrowser) {
        for (JCefBrowser browser : browserList) {
            if (browser.getCefBrowser().equals(cefBrowser)) {
                return browser;
            }
        }
        return null;
    }

    public JCefBrowser getCurrentBrowser() {
        return browserList.get(App.getInstance().getMainFrame().getTabbedPane().getSelectedIndex());
    }
}
