package com.szj.learning.netty.custom.codec.codec;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.szj.learning.netty.custom.codec.msg.Header;
import com.szj.learning.netty.custom.codec.msg.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/22 5:49 下午
 * @Description
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    MarshallingDecoder marshallingDecoder;

    /**
     * 构造函数
     *
     * @param maxFrameLength    最大消息长度
     * @param lengthFieldOffset 消息长度字段的偏移量
     * @param lengthFieldLength 消息长度字段自身的长度
     * @throws IOException
     */
    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 1.先调用 LengthFieldBasedFrameDecoder 的 decode 方法解码出窗口 frame，解决拆包、粘包问题，拿到完整的一个 frame
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        Message message = new Message();

        // 2.解码请求头
        Header header = new Header();
        // 2.1基础字段
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionId(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());

        // 2.2扩展字段
        int size = in.readInt();
        if (size > 0) {
            byte[] keyBytes;
            String key;
            Map<String, Object> attachment = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                // 解析 key
                int keySize = in.readInt();
                keyBytes = new byte[keySize];
                in.readBytes(keyBytes);
                key = new String(keyBytes, StandardCharsets.UTF_8);
                // 解析 value
                Object value = marshallingDecoder.decode(in);
                attachment.put(key, value);
            }
            header.setAttachment(attachment);
        }
        message.setHeader(header);

        // 3.解码请求体
        // 如果长度超过 4，说明是一个对象
        if (in.readableBytes() > 4) {
            message.setBody(marshallingDecoder.decode(in));
        }
        return message;
    }
}
