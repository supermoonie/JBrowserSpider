package com.github.supermoonie.jbrwoserspider.handler;

import com.github.supermoonie.jbrwoserspider.scheme.DefaultSchemeResponseData;
import com.github.supermoonie.jbrwoserspider.scheme.DefaultSchemeResponseHeaders;
import com.github.supermoonie.jbrwoserspider.scheme.Scheme;
import com.github.supermoonie.jbrwoserspider.scheme.SchemePreResponse;
import lombok.extern.slf4j.Slf4j;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

/**
 * @author super_w
 * @since 2021/6/21
 */
@Slf4j
public class SchemeResourceHandler extends CefResourceHandlerAdapter {

    private final Scheme scheme;

    public SchemeResourceHandler(Scheme scm) {
        scheme = scm;
    }

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        SchemePreResponse resp = scheme.processRequest(request);
        switch (resp) {
            case HANDLED_CONTINUE:
                callback.Continue();
                return true;
            case HANDLED_CANCEL:
                callback.cancel();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void getResponseHeaders(CefResponse response, IntRef response_length, StringRef redirectUrl) {
        scheme.getResponseHeaders(new DefaultSchemeResponseHeaders(response, response_length, redirectUrl));
    }

    @Override
    public boolean readResponse(byte[] data_out, int bytes_to_read, IntRef bytes_read, CefCallback callback) {
        return scheme.readResponse(new DefaultSchemeResponseData(data_out, bytes_to_read, bytes_read));
    }

    @Override
    public void cancel() {
        scheme.cancel();
    }
}
