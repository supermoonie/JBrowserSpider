package com.github.supermoonie.jbrwoserspider.scheme;

/**
 * @author super_w
 * @since 2021/6/21
 */
public interface SchemeResponseData {

    byte[] getDataArray();
    int getBytesToRead();
    int getBytesRead();
    void setAmountRead(int rd);
}
