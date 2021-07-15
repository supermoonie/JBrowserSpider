package com.github.supermoonie.jbrwoserspider.setting;

import com.github.supermoonie.jbrwoserspider.util.PropertiesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author super_w
 * @since 2021/7/11
 */
public interface UrlSettings {

    String HOME = PropertiesUtil.getHost() + "/home_index.html";

    Map<String, String> HOST_HOME_MAP = new HashMap<>() {{
        put("www.bilibili.com", PropertiesUtil.getHost() + "/bilibili_index.html");
        put("www.douyin.com", PropertiesUtil.getHost() + "/douyin_index.html");
    }};
}
