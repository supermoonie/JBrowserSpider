package com.github.supermoonie.jbrwoserspider.dialog;

import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author supermoonie
 * @since 2021/2/26
 */
public class DevToolsDialog extends JDialog {

    private final CefBrowser devTools;

    public DevToolsDialog(Frame owner, String title, CefBrowser cefBrowser) {
        this(owner, title, null, cefBrowser);
    }

    public DevToolsDialog(Frame owner, String title, Point inspectAt, CefBrowser cefBrowser) {
        super(owner, title, false);
        devTools = cefBrowser.getDevTools(inspectAt);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocation(owner.getLocation().x + 20, owner.getLocation().y + 20);
        Component uiComponent = devTools.getUIComponent();
        add(uiComponent);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void dispose() {
        devTools.doClose();
        super.dispose();
    }
}
