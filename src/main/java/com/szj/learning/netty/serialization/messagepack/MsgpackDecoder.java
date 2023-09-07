package com.szj.learning.netty.serialization.messagepack;

import com.szj.learning.netty.serialization.User;
import com.szj.learning.netty.serialization.messagepack.MsgpackSerialization.UserTemplate;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final MessagePack msgpack = new MessagePack();
    {
        msgpack.register(User.class, new UserTemplate());
    }

    // 解码 byteBuf 输出到 list
    // messagepack 解码是面向 byte[] 而言的
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        try {
            final int length = byteBuf.readableBytes();
            byte[] bytes = new byte[length];
            // byteBuf 读操作 等同于 byteBuf.readBytes(bytes);
            byteBuf.getBytes(byteBuf.readerIndex(), bytes, 0, length);

            // 解码 read
            list.add(msgpack.read(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
