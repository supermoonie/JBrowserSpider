package com.github.supermoonie.jbrwoserspider.setting;

import lombok.Getter;
import org.apache.commons.lang3.SystemUtils;

/**
 * @author super_w
 * @since 2021/7/17
 */
@Getter
public enum EnumPreferences {

    /**
     * 首选项
     */
    VIDEO_SAVE_PATH("/video/save/path", SystemUtils.getUserHome().getAbsolutePath())
    ;

    private final String key;

    private final Object defaultValue;

    EnumPreferences(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }


}
