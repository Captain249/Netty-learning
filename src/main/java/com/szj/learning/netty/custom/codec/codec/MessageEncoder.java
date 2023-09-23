package com.szj.learning.netty.custom.codec.codec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import com.szj.learning.netty.custom.codec.msg.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/12 4:33 下午
 * @Description MessageToMessageEncoder 放的泛型都是指进入的对象，需要转换的对象
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    private final MarshallingEncoder marshallingEncoder;

    public MessageEncoder() throws IOException {
        marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        if (msg == null || msg.getHeader() == null) {
            throw new RuntimeException("编码失败，没有数据信息");
        }
        // 1.编码请求头 header
        // 1.1 基础字段
        out.writeInt(msg.getHeader().getCrcCode());
        out.writeInt(msg.getHeader().getLength());
        out.writeLong(msg.getHeader().getSessionId());
        out.writeByte(msg.getHeader().getType());
        out.writeByte(msg.getHeader().getPriority());

        // 1.2 扩展字段
        out.writeInt(msg.getHeader().getAttachment().size());
        for (Entry<String, Object> entry : msg.getHeader().getAttachment().entrySet()) {
            // 处理key
            String key = entry.getKey();
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            out.writeInt(keyBytes.length);
            out.writeBytes(keyBytes);
            Object value = entry.getValue();
            marshallingEncoder.encode(value, out);
        }

        // 2.编码请求体
        if (msg.getBody() != null) {
            marshallingEncoder.encode(msg.getBody(), out);
        } else {
            // 如果没有数据 则进行补位 为了方便后续的 decoder操作
            out.writeInt(0);
        }
        // 3.最后更新长度，4 是 lengthFieldOffset
        out.setInt(4, out.readableBytes());
    }

}
