package com.github.supermoonie.jbrwoserspider.www;

import com.github.supermoonie.jbrwoserspider.router.req.ParseHtmlRequest;
import org.cef.browser.CefBrowser;

/**
 * @author super_w
 * @since 2021/6/26
 */
public interface Parser {

    /**
     * 解析视频页面
     *
     * @param browser 浏览器
     * @param request 解析请求
     * @return 解析结果
     * @throws Exception e
     */
    ParseResultPage doParse(CefBrowser browser, ParseHtmlRequest request) throws Exception;
}
