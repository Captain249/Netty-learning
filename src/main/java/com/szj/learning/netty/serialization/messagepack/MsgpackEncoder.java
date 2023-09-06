package com.szj.learning.netty.serialization.messagepack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {

    // 编码 o 最终输出到 byteBuf    write
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MessagePack msgpack = new MessagePack();
        // msgpack 编码直接对对象使用 write 输出为 byte[]
        byte[] bytes = msgpack.write(o);
        byteBuf.writeBytes(bytes);
    }
}
