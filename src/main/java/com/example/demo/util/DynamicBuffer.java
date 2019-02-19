package com.example.demo.util;

/**
 * @author Adam
 * @Description: buffer工具类
 * @Title: DynamicBuffer
 * @ProjectName wanmo-parent
 * @date 2018/10/23 12:51
 */
import java.io.IOException;
import java.io.InputStream;

public class DynamicBuffer {
    private static final int MEMORY_STEP = 1024;
    private byte[] mBuffer = new byte[0];
    private int mOffset = 0;
    private int mLength = 0;

    public DynamicBuffer() {
    }

    public byte[] getBuffer() {
        if(this.mBuffer == null){
            return null;
        }else {
            return this.mBuffer.clone();
        }
    }

    public int getOffset() {
        return this.mOffset;
    }

    public int getLength() {
        return this.mLength;
    }

    public void append(byte[] ab) {
        this.append(ab, 0, ab.length);
    }

    public void increaseSize(int length) {
        if (this.mBuffer.length - this.mOffset < length) {
            byte[] buffer = this.mBuffer;
            length = this.alignSize(length);
            this.mBuffer = new byte[length];
            System.arraycopy(buffer, this.mOffset, this.mBuffer, 0, this.mLength);
            this.mOffset = 0;
        }
    }

    public void append(byte[] ab, int offset, int length) {
        if (this.mLength + length > this.mBuffer.length - this.mOffset) {
            int newLen = this.mLength + length;
            this.increaseSize(newLen);
        }

        System.arraycopy(ab, offset, this.mBuffer, this.mOffset + this.mLength, length);
        this.mLength += length;
    }

    public void write(byte[] ab, int offset, int length) {
        this.append(ab, offset, length);
    }

    public int size() {
        return this.mLength;
    }

    public byte[] toByteArray() {
        byte[] ret = new byte[this.mLength];
        System.arraycopy(this.mBuffer, this.mOffset, ret, 0, this.mLength);
        return ret;
    }

    public void reset() {
        this.mOffset = 0;
        this.mLength = 0;
    }

    public void extrace(int i) {
        if (i >= 0 && i <= this.mLength) {
            this.mOffset += i;
            this.mLength -= i;
        }
    }

    public void append(byte arg0) {
        byte[] ab = new byte[]{arg0};
        this.append(ab);
    }

    public int getSpace() {
        return this.mBuffer.length - (this.mOffset + this.mLength);
    }

    public int append(InputStream is) {
        byte[] ab = new byte[1024];

        while(true) {
            int length;
            try {
                length = is.read(ab);
            } catch (IOException var5) {
                var5.printStackTrace();
                return 1;
            }

            if (length == -1) {
                return 0;
            }

            this.append(ab, 0, length);
        }
    }

    private int alignSize(int size) {
        return size == 0 ? 1024 : ((size - 1) / 1024 + 1) * 1024;
    }
}
