package com.github.supermoonie.jbrwoserspider.util;

import com.github.supermoonie.jbrwoserspider.App;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * @author super_w
 * @since 2021/7/11
 */
@Slf4j
public class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();;

    private PropertiesUtil() {
    }

    static {
        try {
            PROPERTIES.load(App.class.getResourceAsStream("/env.properties"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String getHost() {
        return PROPERTIES.getProperty("host");
    }
}
