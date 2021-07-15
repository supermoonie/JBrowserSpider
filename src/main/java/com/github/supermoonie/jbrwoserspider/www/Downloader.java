package com.github.supermoonie.jbrwoserspider.www;

import com.github.supermoonie.jbrwoserspider.router.req.VideoDownloadReq;
import org.cef.browser.CefBrowser;

/**
 * @author super_w
 * @since 2021/6/27
 */
public interface Downloader {

    /**
     * 视频下载
     *
     * @param req 下载请求
     * @return 下载结果
     * @throws Exception e
     */
    DownloadResult doDownload(CefBrowser browser, VideoDownloadReq req) throws Exception;
}
