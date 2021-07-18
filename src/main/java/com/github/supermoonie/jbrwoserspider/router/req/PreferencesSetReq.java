package com.github.supermoonie.jbrwoserspider.router.req;

import lombok.Data;

/**
 * @author super_w
 * @since 2021/7/17
 */
@Data
public class PreferencesSetReq<T> {

    private String key;

    private T value;
}
