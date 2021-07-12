package com.github.supermoonie.jbrwoserspider.browser;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.handler.CefDisplayHandler;
import com.github.supermoonie.jbrwoserspider.handler.LifeSpanHandler;
import com.github.supermoonie.jbrwoserspider.setting.UrlSettings;
import lombok.Getter;
import org.cef.CefApp;
import org.cef.CefClient;

import javax.swing.*;
import java.util.List;
import java.util.Vector;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;

/**
 * @author super_w
 * @since 2021/7/12
 */
public class JCefClient {

    private static JCefClient INSTANCE;

    @Getter
    private final CefClient defaultCefClient;
    private final List<JCefBrowser> browserList = new Vector<>();

    private JCefClient() {
        defaultCefClient = CefApp.getInstance().createClient();
        defaultCefClient.addLifeSpanHandler(new LifeSpanHandler());
        defaultCefClient.addDisplayHandler(new CefDisplayHandler());
    }

    public static JCefClient getInstance() {
        if (null == INSTANCE) {
            synchronized (JCefClient.class) {
                if (null == INSTANCE) {
                    INSTANCE = new JCefClient();
                }
            }
        }
        return INSTANCE;
    }

    public void createBrowser(String url, boolean showControl, boolean showSpider) {
        JCefBrowser browser = new JCefBrowser(url, showControl, showSpider);
        browserList.add(browser);
        JTabbedPane tabbedPane = App.getInstance().getMainFrame().getTabbedPane();
        if (UrlSettings.HOME.equals(url)) {
            tabbedPane.addTab("Home", new FlatSVGIcon("icons/homeFolder.svg", 16, 16), browser, "主页");
        } else {
            tabbedPane.addTab(".", browser);
        }
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK, (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
            if (browserList.size() == 1) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(App.getInstance().getMainFrame(), "确认退出？", "提示", JOptionPane.YES_NO_OPTION)) {
                    App.getInstance().getExecutor().shutdownNow();
                    CefApp.getInstance().dispose();
                    System.exit(0);
                }
                return;
            }
            browserList.get(tabIndex).getCefBrowser().close(true);
            browserList.remove(tabIndex.intValue());
            tabPane.removeTabAt(tabIndex);
        });
        int lastIndex = tabbedPane.getTabCount() - 1;
        tabbedPane.setSelectedIndex(lastIndex);
    }

    public JCefBrowser getCurrentBrowser() {
        return browserList.get(App.getInstance().getMainFrame().getTabbedPane().getSelectedIndex());
    }
}
