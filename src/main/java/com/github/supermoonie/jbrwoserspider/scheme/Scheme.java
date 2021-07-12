package com.github.supermoonie.jbrwoserspider.scheme;

import org.cef.network.CefRequest;

/**
 * @author super_w
 * @since 2021/6/21
 */
public interface Scheme {

    SchemePreResponse processRequest(CefRequest request);
    void getResponseHeaders(SchemeResponseHeaders resp);
    boolean readResponse(SchemeResponseData data);
    void cancel();
}
