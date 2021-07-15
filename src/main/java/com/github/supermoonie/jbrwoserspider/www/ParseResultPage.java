package com.github.supermoonie.jbrwoserspider.www;

import lombok.Data;

import java.util.List;

/**
 * @author super_w
 * @since 2021/6/29
 */
@Data
public class ParseResultPage {

    private List<ParseResult> resultList;

    private Integer currentIndex;

    private Integer total;

    private Boolean hasMore;
}
