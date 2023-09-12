package com.szj.learning.netty.serialization.messagepack;

import com.szj.learning.netty.serialization.User;
import com.szj.learning.netty.serialization.messagepack.MsgpackSerialization.UserTemplate;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

// 过来的是 Object 出去的是 byteBuf
public class MsgpackEncoder extends MessageToByteEncoder<Object> {

    private static final MessagePack msgpack = new MessagePack();
    {
        msgpack.register(User.class, new UserTemplate());
    }

    // 编码 o 最终输出到 byteBuf    write
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        try{
            // msgpack 编码直接对对象使用 write 输出为 byte[]
            byte[] bytes = msgpack.write(o);
            byteBuf.writeBytes(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
