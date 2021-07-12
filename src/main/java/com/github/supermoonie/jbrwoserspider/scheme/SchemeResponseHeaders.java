package com.github.supermoonie.jbrwoserspider.scheme;

/**
 * @author super_w
 * @since 2021/6/21
 */
public interface SchemeResponseHeaders {

    void setMimeType(String mt);
    void setStatus(int status);
    void setStatusText(String st);
    void setResponseLength(int len);
    void setRedirectURL(String redirectURL);
}
