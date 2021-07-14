package com.github.supermoonie.jbrwoserspider.setting;

import com.github.supermoonie.jbrwoserspider.util.PropertiesUtil;

import java.util.List;
import java.util.Map;

/**
 * @author super_w
 * @since 2021/7/11
 */
public interface UrlSettings {

    String HOME = PropertiesUtil.getHost() + "/home_index.html";

    List<String> SPIDER_HOSTS = List.of("www.bilibili.com");

    Map<String, String> HOST_HOME_MAP = Map.of("www.bilibili.com", PropertiesUtil.getHost() + "/bilibili_index.html");
}
