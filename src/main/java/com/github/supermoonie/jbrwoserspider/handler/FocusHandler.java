package com.github.supermoonie.jbrwoserspider.handler;

import org.cef.browser.CefBrowser;
import org.cef.handler.CefFocusHandlerAdapter;

import java.awt.*;

/**
 * @author super_w
 * @since 2021/7/13
 */
public class FocusHandler extends CefFocusHandlerAdapter {

    @Override
    public void onGotFocus(CefBrowser browser) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        browser.setFocus(true);
    }
}
