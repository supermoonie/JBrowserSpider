package com.github.supermoonie.jbrwoserspider.browser;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.handler.*;
import com.github.supermoonie.jbrwoserspider.router.FileRouter;
import com.github.supermoonie.jbrwoserspider.router.HtmlParseRouter;
import com.github.supermoonie.jbrwoserspider.router.PreferencesRouter;
import com.github.supermoonie.jbrwoserspider.router.VideoDownRouter;
import com.github.supermoonie.jbrwoserspider.setting.UrlSettings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;

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
        log.info("defaultCefClient before created");
        defaultCefClient = CefApp.getInstance().createClient();
        log.info("defaultCefClient created");
        defaultCefClient.addLifeSpanHandler(new LifeSpanHandler());
        defaultCefClient.addDisplayHandler(new CefDisplayHandler());
        defaultCefClient.addFocusHandler(new FocusHandler());
        defaultCefClient.addLoadHandler(new BrowserLoadHandler());
        defaultCefClient.addRequestHandler(new CefRequestHandlerAdapter() {
            @Override
            public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame, CefRequest request, boolean isNavigation, boolean isDownload, String requestInitiator, BoolRef disableDefaultHandling) {
                return new BrowserResourceRequestHandler();
            }
        });
        defaultCefClient.addMessageRouter(FileRouter.getInstance());
        defaultCefClient.addMessageRouter(HtmlParseRouter.getInstance());
        defaultCefClient.addMessageRouter(VideoDownRouter.getInstance());
        defaultCefClient.addMessageRouter(PreferencesRouter.getInstance());
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
                    System.gc();
                }
                return;
            }
            browserList.get(tabIndex).getCefBrowser().close(true);
            browserList.remove(tabIndex.intValue());
            tabPane.removeTabAt(tabIndex);
            System.gc();
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
