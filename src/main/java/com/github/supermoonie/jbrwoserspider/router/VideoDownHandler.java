package com.github.supermoonie.jbrwoserspider.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.httpclient.CustomHttpClient;
import com.github.supermoonie.jbrwoserspider.router.req.VideoDownloadCustomReq;
import com.github.supermoonie.jbrwoserspider.router.req.VideoDownloadReq;
import com.github.supermoonie.jbrwoserspider.setting.EnumVideoFrom;
import com.github.supermoonie.jbrwoserspider.www.DownloadResult;
import com.github.supermoonie.jbrwoserspider.www.Downloader;
import com.github.supermoonie.jbrwoserspider.www.impl.BiliDownloader;
import com.iheartradio.m3u8.*;
import com.iheartradio.m3u8.data.MediaPlaylist;
import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.TrackData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;
import ws.schild.jave.utils.RBufferedReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author super_w
 * @since 2021/6/27
 */
@Slf4j
public class VideoDownHandler extends CefMessageRouterHandlerAdapter {

    private static final String VIDEO_DOWNLOAD = "video:download:";
    private static final String VIDEO_DOWNLOAD_CUSTOM = "video:download:custom:";

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
            if (request.startsWith(VIDEO_DOWNLOAD_CUSTOM)) {
                onVideoCustomDownload(browser, request, callback);
                return true;
            } else if (request.startsWith(VIDEO_DOWNLOAD)) {
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

    // https://pro.coolcollege.cn/?eid=1372001210185420819#/course/watch?courseId=1791325853397946368&exitAiUpdate=false&resourceId=1791319240620511232&source=enterprise&storage_type=PUBLIC_CLOUD&videoId=9d6dbe1392b7499690d9fea986e7e1fe
    private void onVideoCustomDownload(CefBrowser browser, String request, CefQueryCallback callback) {
        App.getInstance().getExecutor().execute(() -> {
            try {
                String req = request.replace(VIDEO_DOWNLOAD_CUSTOM, "");
                log.info("req: {}", req);
                final VideoDownloadCustomReq downloadReq = JSON.parseObject(req, VideoDownloadCustomReq.class);
                CustomHttpClient httpClient = new CustomHttpClient();
                RequestBuilder requestBuilder = RequestBuilder.get(downloadReq.getUrl());
                Map<String, String> headers = JSON.parseObject(downloadReq.getHeaders(), new TypeReference<>() {
                });
                requestBuilder.setHeader("User-Agent", App.UA);
                File targetFolder = new File(downloadReq.getPath());
                File targetFile = new File(downloadReq.getPath() + File.separator + downloadReq.getName() + ".mp4");
                if (downloadReq.getContentType().equals("video/mp4")) {
                    httpClient.execute(requestBuilder.build(), (ResponseHandler<Void>) response -> {
                        HttpEntity entity = response.getEntity();
                        long contentLength = entity.getContentLength();
                        InputStream is = entity.getContent();
                        byte[] buf = new byte[1024];
                        long readSize = 0;
                        int size;
                        FileOutputStream fos = new FileOutputStream(targetFile);
                        List<Long> send = new ArrayList<>();
                        while ((size = is.read(buf)) != -1) {
                            fos.write(buf, 0, size);
                            readSize = readSize + size;
                            long progress = (readSize * 100L) / contentLength;
                            if (send.contains(progress)) {
                                continue;
                            }
                            send.add(progress);
                            log.info("{} : {}", downloadReq.getName(), progress);
                            if (progress % 10 == 0) {
                                browser.executeJavaScript(String.format("window._COMMON.setDownloadProgress('%s', %d)", downloadReq.getVideoId(), progress), "", 0);
                            }
                        }
                        browser.executeJavaScript(String.format("window._COMMON.setDownloadProgress('%s', %d)", downloadReq.getVideoId(), 100), "", 0);
                        fos.flush();
                        fos.close();
                        return null;
//                        byte[] bytes = EntityUtils.toByteArray(response.getEntity());
//                        FileUtils.writeByteArrayToFile(targetFile, bytes);
//                        return null;
                    });
                } else {
                    Playlist playlist = httpClient.execute(requestBuilder.build(), response -> {
                        HttpEntity entity = response.getEntity();
                        PlaylistParser parser = new PlaylistParser(entity.getContent(), Format.EXT_M3U, Extension.M3U8, ParsingMode.LENIENT);
                        try {
                            return parser.parse();
                        } catch (ParseException | PlaylistException e) {
                            log.error(e.getMessage(), e);
                            return null;
                        }
                    });
                    if (null == playlist) {
                        callback.failure(-500, "playlist is empty");
                        return;
                    }
                    List<TrackData> tracks = playlist.getMediaPlaylist().getTracks();
                    MediaPlaylist mediaPlaylist = playlist.getMediaPlaylist();
                    AtomicInteger count = new AtomicInteger(1000);
                    int size = tracks.size();
                    String randomId = UUID.randomUUID().toString().replaceAll("-", "");
                    List<File> fileList = new ArrayList<>();
                    tracks.forEach(track -> {
                        try {
                            File video = Request.Get(track.getUri()).userAgent(App.UA)
                                    .execute().handleResponse(res -> {
                                        byte[] bytes = EntityUtils.toByteArray(res.getEntity());
                                        int index = count.getAndIncrement();
                                        File ts = new File(targetFolder.getAbsolutePath() + File.separator + randomId + "_" + index + ".ts");
                                        FileUtils.writeByteArrayToFile(ts, bytes);
                                        return ts;
                                    });
                            fileList.add(video);
                        } catch (IOException e) {
                           log.error(e.getMessage(), e);
                        }
                    });
                    try {
                        File fileListTxt = new File(targetFolder.getAbsolutePath() + File.separator + randomId + ".txt");
                        FileUtils.writeLines(fileListTxt, fileList.stream().map(file -> String.format("file '%s'", file.getAbsolutePath())).collect(Collectors.toList()));
                        DefaultFFMPEGLocator locator = new DefaultFFMPEGLocator();
                        String tempFileName = System.currentTimeMillis() + ".mp4";
                        ProcessWrapper ffmpeg = locator.createExecutor();
                        ffmpeg.addArgument("-f");
                        ffmpeg.addArgument("concat");
                        ffmpeg.addArgument("-safe");
                        ffmpeg.addArgument("0");
                        ffmpeg.addArgument("-i");
                        ffmpeg.addArgument(fileListTxt.getAbsolutePath());
                        ffmpeg.addArgument("-c");
                        ffmpeg.addArgument("copy");
                        ffmpeg.addArgument(targetFolder.getAbsolutePath() + File.separator + tempFileName);
                        try {
                            ffmpeg.execute();
                            RBufferedReader reader = new RBufferedReader(new InputStreamReader(ffmpeg.getErrorStream()));
                            int lineNumber = 0;
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lineNumber++;
                                System.out.println("line: " + lineNumber + ", " + line);
                            }
                            fileList.forEach(FileUtils::deleteQuietly);
                            FileUtils.deleteQuietly(fileListTxt);
                            FileUtils.moveFile(new File(targetFolder.getAbsolutePath() + File.separator + tempFileName), targetFile);
                        } finally {
                            ffmpeg.destroy();
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                callback.success("");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                callback.failure(-1, e.getMessage());
            }
        });
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
