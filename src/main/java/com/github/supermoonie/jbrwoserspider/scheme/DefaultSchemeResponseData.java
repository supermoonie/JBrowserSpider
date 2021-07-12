package com.github.supermoonie.jbrwoserspider.scheme;

import org.cef.misc.IntRef;

/**
 * @author super_w
 * @since 2021/6/21
 */
public class DefaultSchemeResponseData implements SchemeResponseData {

    private final byte[] data;
    private final int toRead;
    private final IntRef read;

    public DefaultSchemeResponseData(byte[] data, int toRead, IntRef read) {
        this.data = data;
        this.toRead = toRead;
        this.read = read;
    }

    @Override
    public byte[] getDataArray() {
        return data;
    }

    @Override
    public int getBytesToRead() {
        return toRead;
    }

    @Override
    public int getBytesRead() {
        return read.get();
    }

    @Override
    public void setAmountRead(int rd) {
        read.set(rd);
    }
}
