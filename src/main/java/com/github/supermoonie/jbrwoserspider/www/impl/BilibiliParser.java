package com.github.supermoonie.jbrwoserspider.www.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.httpclient.CustomHttpClient;
import com.github.supermoonie.jbrwoserspider.router.req.ParseHtmlRequest;
import com.github.supermoonie.jbrwoserspider.www.ParseResult;
import com.github.supermoonie.jbrwoserspider.www.ParseResultPage;
import com.github.supermoonie.jbrwoserspider.www.Parser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.cef.browser.CefBrowser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;

/**
 * @author super_w
 * @since 2021/6/26
 */
@Slf4j
public class BilibiliParser implements Parser {

    @Override
    public ParseResultPage doParse(CefBrowser browser, ParseHtmlRequest request) throws Exception {
        String currentUrl = browser.getURL();
        URL url = new URL(currentUrl);
        String path = url.getPath();
        ParseResultPage page = new ParseResultPage();
        page.setResultList(new ArrayList<>());
        // 视频列表
        if (path.startsWith("/medialist/play/")) {
            String bizId = path.replaceAll("/medialist/play/", "");
            String tempListUrl = "https://api.bilibili.com/x/v2/medialist/resource/list?type=1&otype=2&biz_id=%s&bvid=%s&with_current=true&mobi_app=web&ps=20&direction=false&sort_field=1&tid=0&desc=true";
            String listUrl = String.format(tempListUrl, bizId, null == request.getLastId() ? "" : request.getLastId());
            log.info("listUrl: {}", listUrl);
            JSONObject listJson = Request.Get(listUrl).userAgent(App.UA).addHeader("Referer", "https://www.bilibili.com/")
                    .execute().handleResponse(httpResponse -> JSONObject.parseObject(EntityUtils.toString(httpResponse.getEntity())));
            JSONObject dataJson = listJson.getJSONObject("data");
            if (null == dataJson) {
                page.setCurrentIndex(request.getCurrentIndex());
                page.setTotal(request.getTotal());
                page.setHasMore(false);
                return page;
            }
            JSONArray jsonArray = dataJson.getJSONArray("media_list");
            if (0 == jsonArray.size()) {
                page.setCurrentIndex(request.getCurrentIndex());
                page.setTotal(request.getTotal());
                page.setHasMore(false);
                return page;
            }
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject itemJson = jsonArray.getJSONObject(j);
                String bvId = itemJson.getString("bv_id");
                currentUrl = "https://www.bilibili.com/video/" + bvId;
                ParseResult result = parseItem(currentUrl);
                page.getResultList().add(result);
            }
            Boolean hasMore = dataJson.getBoolean("has_more");
            page.setHasMore(hasMore);
            page.setTotal(dataJson.getInteger("total_count"));
            page.setCurrentIndex(request.getCurrentIndex());
            return page;
        } else if (currentUrl.startsWith("https://www.bilibili.com/video/")) {
            ParseResult result = parseItem(currentUrl);
            page.getResultList().add(result);
            page.setHasMore(false);
            page.setCurrentIndex(0);
            page.setTotal(1);
            return page;
        }
        return null;
    }

    public ParseResult parseItem(String currentUrl) throws Exception {
        log.info("currentUrl: {}", currentUrl);
        CustomHttpClient httpClient = new CustomHttpClient();
        Document doc = httpClient.execute(RequestBuilder.get(currentUrl)
                .addHeader("Referer", "https://www.bilibili.com")
                .build(), response -> Jsoup.parse(EntityUtils.toString(response.getEntity())));
        Elements scripts = doc.getElementsByTag("script");
        Element playInfoEle = scripts.stream().filter(script -> script.html().contains("window.__playinfo__"))
                .findFirst().orElse(null);
        if (null == playInfoEle) {
            log.info(doc.toString());
            throw new RuntimeException("未解析到视频信息");
        }
        String playInfo = playInfoEle.html().replace("window.__playinfo__=", "");
        JSONObject playInfoJson = JSONObject.parseObject(playInfo);
        Element initialStateEle = scripts.stream().filter(script -> script.html().contains("window.__INITIAL_STATE__"))
                .findFirst().orElse(null);
        if (null == initialStateEle) {
            throw new RuntimeException("未解析到视频信息");
        }
        String initialState = initialStateEle.html()
                .replace("window.__INITIAL_STATE__=", "")
                .replaceAll(";\\(function.*;", "");
        JSONObject initialStateJson = JSONObject.parseObject(initialState);
        ParseResult result = new ParseResult();
        result.setId(initialStateJson.getString("bvid"));
        result.setTitle(initialStateJson.getJSONObject("videoData").getString("title"));
        result.setContent(playInfoJson);
        return result;
    }
}
