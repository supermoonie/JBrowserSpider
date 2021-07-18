package com.github.supermoonie.jbrwoserspider.handler;

import com.alibaba.fastjson.JSON;
import com.github.supermoonie.jbrwoserspider.browser.JCefBrowser;
import com.github.supermoonie.jbrwoserspider.browser.JCefClient;
import lombok.extern.slf4j.Slf4j;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefResourceRequestHandlerAdapter;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.cef.network.CefURLRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author super_w
 * @since 2021/7/15
 */
@Slf4j
public class BrowserResourceRequestHandler extends CefResourceRequestHandlerAdapter {

    private static final List<String> VIDEO_TYPES = List.of("application/vnd.apple.mpegurl", "video/mp4");

    @Override
    public boolean onBeforeResourceLoad(CefBrowser browser, CefFrame frame, CefRequest request) {
        return super.onBeforeResourceLoad(browser, frame, request);
    }

    @Override
    public void onResourceLoadComplete(CefBrowser browser, CefFrame frame, CefRequest request, CefResponse response, CefURLRequest.Status status, long receivedContentLength) {
        String contentType = response.getMimeType();
        final String url = request.getURL();
        if (VIDEO_TYPES.contains(contentType)) {
            JCefBrowser jCefBrowser = JCefClient.getInstance().getBrowser(browser);
            if (null == jCefBrowser) {
                return;
            }
            Map<String, String> headers = new HashMap<>();
            request.getHeaderMap(headers);
            CefBrowser workCefBrowser = jCefBrowser.getWorkCefBrowser();
            workCefBrowser.executeJavaScript(String.format("window._COMMON.setUrl('%s')", url), null, 0);
            workCefBrowser.executeJavaScript(String.format("window._COMMON.setFileName('%s')", jCefBrowser.getTitle()), null, 0);
            workCefBrowser.executeJavaScript(String.format("window._COMMON.setMediaType('%s')", contentType), null, 0);
            workCefBrowser.executeJavaScript(String.format("window._COMMON.setHeaders('%s')", JSON.toJSONString(headers)), null, 0);
        }
    }
}
