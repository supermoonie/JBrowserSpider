package com.github.supermoonie.jbrwoserspider.scheme;

import com.github.supermoonie.jbrwoserspider.mime.MimeMappings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.cef.network.CefRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author super_w
 * @since 2021/6/21
 */
@Slf4j
public class ClientScheme implements Scheme {

    private String contentType = null;
    private InputStream is = null;

    @Override
    public SchemePreResponse processRequest(CefRequest request) {
//        log.info("req: {}", request);
        String url = "/client/" + request.getURL().substring("https://client/".length());
//        log.info("url: {}", url);
        is = ClientScheme.class.getResourceAsStream(url);
        if (is == null) {
            log.warn("Resource " + url + " NOT found!");
            return SchemePreResponse.NOT_HANDLED;
        }
        contentType = null;
        int pos = url.lastIndexOf('.');
        if (pos >= 0 && pos < url.length() - 2)
            contentType = MimeMappings.DEFAULT.get(url.substring(pos + 1));

        return SchemePreResponse.HANDLED_CONTINUE;
    }

    @Override
    public void getResponseHeaders(SchemeResponseHeaders resp) {
        if (contentType != null)
            resp.setMimeType(contentType);

        resp.setStatus(200);
        resp.setStatusText("OK");
        resp.setResponseLength(-1);
    }

    @Override
    public boolean readResponse(SchemeResponseData data) {
        try {
            int ret = is.read(data.getDataArray(), 0, data.getBytesToRead());
            if (ret <= 0)
                is.close();
            data.setAmountRead(Math.max(ret, 0));
            return ret > 0;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void cancel() {
        IOUtils.closeQuietly(is, e -> log.error(e.getMessage(), e));
    }
}
