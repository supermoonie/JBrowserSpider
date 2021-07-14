package com.github.supermoonie.jbrwoserspider.listener;

import com.github.supermoonie.jbrwoserspider.browser.JCefBrowser;
import com.github.supermoonie.jbrwoserspider.browser.JCefClient;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * @author super_w
 * @since 2021/7/13
 */
public class GlobalKeyListener implements NativeKeyListener {

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_F12) {
            JCefBrowser cefBrowser = JCefClient.getInstance().getCurrentBrowser();
            cefBrowser.getDevTools().setVisible(true);
        } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_F11) {
            JCefBrowser cefBrowser = JCefClient.getInstance().getCurrentBrowser();
            if (null != cefBrowser.getWorkDevTools()) {
                cefBrowser.getWorkDevTools().setVisible(true);
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
