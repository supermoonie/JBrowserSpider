package com.github.supermoonie.jbrwoserspider.scheme;

import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefResponse;

/**
 * @author super_w
 * @since 2021/6/21
 */
public class DefaultSchemeResponseHeaders implements SchemeResponseHeaders {

    private final CefResponse response;
    private final IntRef length;
    private final StringRef redirectURL;

    public DefaultSchemeResponseHeaders(CefResponse r, IntRef l, StringRef url) {
        response = r;
        length = l;
        redirectURL = url;
    }

    @Override
    public void setMimeType(String mt) {
        response.setMimeType(mt);
    }

    @Override
    public void setStatus(int status) {
        response.setStatus(status);
    }

    @Override
    public void setStatusText(String st) {
        response.setStatusText(st);
    }

    @Override
    public void setResponseLength(int len) {
        length.set(len);
    }

    @Override
    public void setRedirectURL(String r) {
        redirectURL.set(r);
    }
}
