package com.szj.learning.netty.custom.codec.codec;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/22 4:41 下午
 * @Description
 */
public class ChannelBufferByteInput implements ByteInput {

    private final ByteBuf byteBuf;

    public ChannelBufferByteInput(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public int read() throws IOException {
        if (byteBuf.isReadable()) {
            return byteBuf.readByte() & 0xff;
        }
        return -1;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] dst, int dstIndex, int length) throws IOException {
        int available = available();
        if (available == 0) {
            return -1;
        }
        length = Math.min(available, length);
        byteBuf.readBytes(dst, dstIndex, length);
        return length;
    }

    @Override
    public int available() throws IOException {
        return byteBuf.readableBytes();
    }

    @Override
    public long skip(long bytes) throws IOException {
        int readable = byteBuf.readableBytes();
        if (readable < bytes) {
            bytes = readable;
        }
        byteBuf.readerIndex((int) (byteBuf.readerIndex() + bytes));
        return bytes;
    }

    @Override
    public void close() throws IOException {

    }
}
