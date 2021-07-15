package com.github.supermoonie.jbrwoserspider.setting;

import java.util.Arrays;

/**
 * @author super_w
 * @since 2021/6/27
 */
public enum EnumVideoFrom {

    /**
     * 视频来源
     */
    BILI("bili")
    ;

    private final String value;

    EnumVideoFrom(String value) {
        this.value = value;
    }

    public static EnumVideoFrom of(String value) {
        return Arrays.stream(EnumVideoFrom.values()).filter(item -> item.value.equals(value)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "EnumVideoFrom{" +
                "value='" + value + '\'' +
                '}';
    }
}
