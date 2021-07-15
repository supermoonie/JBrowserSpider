package com.github.supermoonie.jbrwoserspider.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.browser.JCefClient;
import com.github.supermoonie.jbrwoserspider.router.req.ParseHtmlRequest;
import com.github.supermoonie.jbrwoserspider.www.ParseResultPage;
import com.github.supermoonie.jbrwoserspider.www.Parser;
import com.github.supermoonie.jbrwoserspider.www.impl.BilibiliParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

/**
 * @author super_w
 * @since 2021/6/26
 */
@Slf4j
public class HtmlParseHandler extends CefMessageRouterHandlerAdapter {

    private static final String HTML_PARSE = "html:parse:";

    private static CefMessageRouter htmlParseHandler;

    private HtmlParseHandler() {

    }

    public static CefMessageRouter getInstance() {
        if (null == htmlParseHandler) {
            synchronized (BrowserRouter.class) {
                if (null == htmlParseHandler) {
                    htmlParseHandler = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("htmlParseQuery", "cancelHtmlParseQuery"));
                    htmlParseHandler.addHandler(new HtmlParseHandler(), true);
                }
            }
        }
        return htmlParseHandler;
    }

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            if (request.startsWith(HTML_PARSE)) {
                onHtmlParse(browser, request, callback);
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

    private void onHtmlParse(CefBrowser browser, String request, CefQueryCallback callback) {
        App.getInstance().getExecutor().execute(() -> {
            try {
                ParseHtmlRequest req = JSONObject.parseObject(request.replace(HTML_PARSE, ""), ParseHtmlRequest.class);
                String url = JCefClient.getInstance().getCurrentBrowser().getCefBrowser().getURL();
                if (StringUtils.isEmpty(url)) {
                    callback.failure(-1, "当前页面无视频");
                    return;
                }
                if (url.contains("www.bilibili.com")) {
                    Parser parser = new BilibiliParser();
                    ParseResultPage page = parser.doParse(JCefClient.getInstance().getCurrentBrowser().getCefBrowser(), req);
                    if (null == page) {
                        callback.failure(-1, "当前页未解析出视频");
                    } else {
                        callback.success(JSON.toJSONString(page));
                    }
                    return;
                }
                callback.failure(-1, "当前页面不支持, url: " + url);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                callback.failure(-1, e.getMessage());
            }

        });
    }
}
