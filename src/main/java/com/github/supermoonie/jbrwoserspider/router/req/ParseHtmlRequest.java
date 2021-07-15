package com.github.supermoonie.jbrwoserspider.router.req;

import lombok.Data;

/**
 * @author super_w
 * @since 2021/6/30
 */
@Data
public class ParseHtmlRequest {

    private Integer currentIndex;

    private Integer total;

    private String lastId;
}
