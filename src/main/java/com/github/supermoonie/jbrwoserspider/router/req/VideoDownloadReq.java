package com.github.supermoonie.jbrwoserspider.router.req;

import lombok.Data;

/**
 * @author super_w
 * @since 2021/6/27
 */
@Data
public class VideoDownloadReq {

    private String videoFrom;

    private String videoUrl;

    private String audioUrl;

    private String videoId;

    private String savePath;

    private String videoName;
}
