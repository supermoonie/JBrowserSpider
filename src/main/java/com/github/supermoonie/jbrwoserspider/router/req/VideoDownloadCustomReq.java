package com.github.supermoonie.jbrwoserspider.router.req;

import lombok.Data;

/**
 * @author super_w
 * @since 2021/7/15
 */
@Data
public class VideoDownloadCustomReq {

    private String url;

    private String path;

    private String name;

    private String contentType;

    private String headers;
}
