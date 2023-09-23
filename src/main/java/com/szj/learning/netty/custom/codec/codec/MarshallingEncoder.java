package com.szj.learning.netty.custom.codec.codec;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/12 5:42 下午
 * @Description 参照 io.netty.handler.codec.marshalling.MarshallingEncoder 的写法，需要有一个 encode 方法将对象输出到自定义的 ButeBuf 中
 */
public class MarshallingEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodeCFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws IOException {
        try {
            int lengthStartPosition = out.writerIndex();
            // 长度的占位字节，在后续编码完成后，将会根据实际消息长度来更新这个占位符。
            out.writeBytes(LENGTH_PLACEHOLDER);
            ChannelBufferByteOutput byteOutput = new ChannelBufferByteOutput(out);
            marshaller.start(byteOutput);
            marshaller.writeObject(msg);
            marshaller.finish();
            // 根据实际编码后的消息长度来更新之前写入的占位符 LENGTH_PLACEHOLDER，以确保消息头部包含了正确的消息长度。
            // 给长度的 4 字节，写入 body 的具体长度，out.writerIndex() - lengthPos - 4 是 body 的长度
            out.setInt(lengthStartPosition, out.writerIndex() - lengthStartPosition - 4);
        } finally {
            marshaller.close();
        }
    }
}
