package com.github.supermoonie.jbrwoserspider.setting;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author super_w
 * @since 2021/7/14
 */
public class UrlSettingsTest {

    @Test
    public void url() throws MalformedURLException {
        String url = "https://www.bilibili.com/medialist/play/11173348?from=space&business=space&sort_field=pubtime&tid=0";
        URL u = new URL(url);
        System.out.println(u.getHost());
    }

}