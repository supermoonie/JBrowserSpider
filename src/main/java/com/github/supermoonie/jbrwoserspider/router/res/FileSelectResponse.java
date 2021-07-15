package com.github.supermoonie.jbrwoserspider.router.res;

import lombok.Data;

/**
 * @author supermoonie
 * @since 2021/3/4
 */
@Data
public class FileSelectResponse {

    private String path;

    private Long size;

    private Long modifyDate;

    private String fileName;

    private Integer width;

    private Integer height;
}
