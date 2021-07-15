package com.github.supermoonie.jbrwoserspider.www;

import lombok.Data;

/**
 * @author super_w
 * @since 2021/6/27
 */
@Data
public class DownloadResult {

    private String videoId;

    private String videoFom;

    private Boolean success;

    private String error;
}
