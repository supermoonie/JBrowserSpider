package com.github.supermoonie.jbrwoserspider.router;

import com.github.supermoonie.jbrwoserspider.App;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

/**
 * @author super_w
 * @since 2021/7/13
 */
@Slf4j
public class BrowserRouter extends CefMessageRouterHandlerAdapter {

    private static final String BROWSER_CREATE = "browser:create:";

    private static CefMessageRouter browserRouter;

    private BrowserRouter() {

    }

    public CefMessageRouter getInstance() {
        if (null == browserRouter) {
            synchronized (BrowserRouter.class) {
                if (null == browserRouter) {
                    browserRouter = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("browserQuery", "cancelBrowserQuery"));
                    browserRouter.addHandler(new BrowserRouter(), true);
                }
            }
        }
        return browserRouter;
    }

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            if (request.equals(BROWSER_CREATE)) {
                onBrowserCreate(request, callback);
                return true;
            }
            callback.failure(404, "no cmd found");
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            callback.failure(500, e.getMessage());
            return false;
        }
    }

    private void onBrowserCreate(String request, CefQueryCallback callback) {
        String req = request.replace(BROWSER_CREATE, "");
        if (StringUtils.isEmpty(req)) {
            callback.failure(405, "cmd: " + BROWSER_CREATE + " args is empty!");
            return;
        }
        App.getInstance().getExecutor().execute(() -> {
//            JCefClient.getInstance().createBrowser();
        });
    }

}
