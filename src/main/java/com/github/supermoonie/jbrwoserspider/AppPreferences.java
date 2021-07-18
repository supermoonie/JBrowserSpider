package com.github.supermoonie.jbrwoserspider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.prefs.Preferences;

/**
 * @author super_w
 * @since 2021/7/17
 */
@Slf4j
public class AppPreferences {

    private static final String ROOT_PATH = "/j_browser_spider";

    public static final String KEY_VIDEO_SAVE_PATH = "/key/video/save/path";
    @Getter
    private static Preferences state;

    public static void init() {
        state = Preferences.userRoot().node(ROOT_PATH);
    }
}
