package com.szj.learning.netty.custom.codec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import com.szj.learning.netty.serialization.marshalling.MslCodecFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/12 4:33 下午
 * @Description
 */
// MessageToMessageEncoder 放的泛型都是指进入的对象，需要转换的对象
public class MessageEncoder extends MessageToMessageEncoder<Message> {

    private final MarshallingEncoder marshallingEncoder;

    public MessageEncoder() throws IOException {
        marshallingEncoder = MslCodecFactory.buildMslEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        // 基础字段
        buffer.writeInt(msg.getHeader().getCrcCode());
        buffer.writeInt(msg.getHeader().getLength());
        buffer.writeLong(msg.getHeader().getSessionId());
        buffer.writeByte(msg.getHeader().getType());
        buffer.writeByte(msg.getHeader().getPriority());

        // 扩展字段
        buffer.writeInt(msg.getHeader().getAttachment().size());
        for (Entry<String, Object> entry : msg.getHeader().getAttachment().entrySet()) {
            // 处理key
            String key = entry.getKey();
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            buffer.writeInt(keyBytes.length);
            buffer.writeBytes(keyBytes);
            Object value = entry.getValue();
        }
    }

}
