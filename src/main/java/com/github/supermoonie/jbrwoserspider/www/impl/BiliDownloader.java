package com.github.supermoonie.jbrwoserspider.www.impl;

import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.httpclient.CustomHttpClient;
import com.github.supermoonie.jbrwoserspider.router.req.VideoDownloadReq;
import com.github.supermoonie.jbrwoserspider.www.DownloadResult;
import com.github.supermoonie.jbrwoserspider.www.Downloader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.cef.browser.CefBrowser;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;
import ws.schild.jave.utils.RBufferedReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author super_w
 * @since 2021/6/27
 */
@Slf4j
public class BiliDownloader implements Downloader {

    @Override
    public DownloadResult doDownload(CefBrowser browser, VideoDownloadReq req) throws Exception {
        String time = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        File tempAudioFile = new File(req.getSavePath() + File.separator + time + ".mp3");
        File tempVideoFile = new File(req.getSavePath() + File.separator + time + ".mp4");
        File tempTargetFile = new File(req.getSavePath() + File.separator + UUID.randomUUID().toString().replaceAll("-", "") + ".mp4");
        File targetFile = new File(req.getSavePath() + File.separator + req.getVideoName() + ".mp4");
        CustomHttpClient httpClient = new CustomHttpClient();
        httpClient.execute(RequestBuilder.get(req.getAudioUrl())
                .addHeader("Referer", "https://www.bilibili.com/")
                .addHeader("Range", "bytes=0-").build(), response -> {
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            FileUtils.writeByteArrayToFile(tempAudioFile, bytes);
            return null;
        });
        log.info("video url: {}", req.getVideoUrl());
        httpClient.execute(RequestBuilder.get(req.getVideoUrl())
                .addHeader("Referer", "https://www.bilibili.com/")
                .addHeader("Range", "bytes=0-")
                .build(), response -> {
            HttpEntity entity = response.getEntity();
            long contentLength = entity.getContentLength();
            InputStream is = entity.getContent();
            byte[] buf = new byte[1024];
            long readSize = 0;
            int size;
            FileOutputStream fos = new FileOutputStream(tempVideoFile);
            List<Long> send = new ArrayList<>();
            while ((size = is.read(buf)) != -1) {
                fos.write(buf, 0, size);
                readSize = readSize + size;
                long progress = (readSize * 100L) / contentLength;
                if (send.contains(progress)) {
                    continue;
                }
                send.add(progress);
                log.info("{} : {}", req.getVideoName(), progress);
                if (progress % 10 == 0) {
                    browser.executeJavaScript(String.format("window.BILI.setDownloadProgress('%s', %d)", req.getVideoId(), progress), "", 0);
                }
            }
            browser.executeJavaScript(String.format("window.BILI.setDownloadProgress('%s', %d)", req.getVideoId(), 100), "", 0);
            fos.flush();
            fos.close();
            return null;
        });
        DefaultFFMPEGLocator locator = new DefaultFFMPEGLocator();
        ProcessWrapper ffmpeg = locator.createExecutor();
        ffmpeg.addArgument("-i");
        ffmpeg.addArgument(tempAudioFile.getAbsolutePath());
        ffmpeg.addArgument("-i");
        ffmpeg.addArgument(tempVideoFile.getAbsolutePath());
        ffmpeg.addArgument("-vcodec");
        ffmpeg.addArgument("copy");
        ffmpeg.addArgument("-acodec");
        ffmpeg.addArgument("copy");
        ffmpeg.addArgument(tempTargetFile.getAbsolutePath());
        try {
            ffmpeg.execute();
            RBufferedReader reader = new RBufferedReader(new InputStreamReader(ffmpeg.getErrorStream()));
            int lineNumber = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                System.out.println("line: " + lineNumber + ", " + line);
            }
            FileUtils.moveFile(tempTargetFile, targetFile);
        } finally {
            ffmpeg.destroy();
            tempAudioFile.delete();
            tempVideoFile.delete();
        }
        return null;
    }
}
