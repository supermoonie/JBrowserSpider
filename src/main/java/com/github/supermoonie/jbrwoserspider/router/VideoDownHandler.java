package com.github.supermoonie.jbrwoserspider.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.router.req.VideoDownloadReq;
import com.github.supermoonie.jbrwoserspider.setting.EnumVideoFrom;
import com.github.supermoonie.jbrwoserspider.www.DownloadResult;
import com.github.supermoonie.jbrwoserspider.www.Downloader;
import com.github.supermoonie.jbrwoserspider.www.impl.BiliDownloader;
import lombok.extern.slf4j.Slf4j;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

/**
 * @author super_w
 * @since 2021/6/27
 */
@Slf4j
public class VideoDownHandler extends CefMessageRouterHandlerAdapter {

    private static final String VIDEO_DOWNLOAD = "video:download:";

    private static CefMessageRouter videoDownHandler;

    private VideoDownHandler() {

    }

    public static CefMessageRouter getInstance() {
        if (null == videoDownHandler) {
            synchronized (BrowserRouter.class) {
                if (null == videoDownHandler) {
                    videoDownHandler = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("videoDownloadQuery", "cancelVideoDownloadQuery"));
                    videoDownHandler.addHandler(new VideoDownHandler(), true);
                }
            }
        }
        return videoDownHandler;
    }

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            if (request.startsWith(VIDEO_DOWNLOAD)) {
                onVideoDownload(browser, request, callback);
                return true;
            }
            callback.failure(404, "no cmd found");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            callback.failure(500, e.getMessage());
            return true;
        }
    }

    private void onVideoDownload(CefBrowser browser, String request, CefQueryCallback callback) {
        App.getInstance().getExecutor().execute(() -> {
            try {
                String req = request.replace(VIDEO_DOWNLOAD, "");
                final VideoDownloadReq downloadReq = JSONObject.parseObject(req, VideoDownloadReq.class);
                if (EnumVideoFrom.BILI == EnumVideoFrom.of(downloadReq.getVideoFrom())) {
                    Downloader downloader = new BiliDownloader();
                    DownloadResult downloadResult = downloader.doDownload(browser, downloadReq);
                    callback.success(JSON.toJSONString(downloadResult));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                callback.failure(-1, e.getMessage());
            }
        });
    }
}
