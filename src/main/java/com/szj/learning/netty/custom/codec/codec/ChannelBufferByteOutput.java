package com.szj.learning.netty.custom.codec.codec;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.jboss.marshalling.ByteOutput;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/22 11:28 上午
 * @Description
 */
@Getter
public class ChannelBufferByteOutput implements ByteOutput {

    private ByteBuf byteBuf;

    public ChannelBufferByteOutput(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public void write(int b) throws IOException {
        byteBuf.writeByte(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        byteBuf.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byteBuf.writeBytes(b, off, len);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }
}
