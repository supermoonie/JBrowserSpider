package com.github.supermoonie.jbrwoserspider.handler;

import com.github.supermoonie.jbrwoserspider.scheme.ClientScheme;
import com.github.supermoonie.jbrwoserspider.scheme.Scheme;
import lombok.extern.slf4j.Slf4j;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefCommandLine;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

/**
 * @author super_w
 * @since 2021/6/15
 */
@Slf4j
public class AppHandler extends CefAppHandlerAdapter {

    public AppHandler(String[] args) {
        super(args);
    }

    @Override
    public void stateHasChanged(CefApp.CefAppState state) {
        if (state == CefApp.CefAppState.TERMINATED) {
            System.exit(0);
        }
    }

    @Override
    public void onBeforeCommandLineProcessing(String processType, CefCommandLine commandLine) {
        commandLine.appendSwitch("disable-web-security");
        commandLine.appendSwitch("–allow-file-access-from-files");
        commandLine.appendSwitch("--enable-local-file-accesses");
        super.onBeforeCommandLineProcessing(processType, commandLine);
    }

    @Override
    public void onContextInitialized() {
        CefApp.getInstance().registerSchemeHandlerFactory("https", "client", new SchemeHandlerFactory(ClientScheme.class));
    }

    private static class SchemeHandlerFactory implements CefSchemeHandlerFactory {

        private final Class<? extends Scheme> cls;

        private SchemeHandlerFactory(Class<? extends Scheme> cls) {
            this.cls = cls;
        }

        @Override
        public CefResourceHandler create(CefBrowser browser, CefFrame frame, String schemeName, CefRequest request) {
            try {
                return new SchemeResourceHandler(cls.getDeclaredConstructor().newInstance());
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        }

    }
}
