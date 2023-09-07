package com.szj.learning.netty.serialization.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.szj.learning.netty.serialization.protobuf.UserProto.User;
import com.szj.learning.netty.serialization.protobuf.UserProto.User.Builder;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/7 5:31 下午
 * @Description
 */
public class ProtobufDemo {

    private static byte[] encode(UserProto.User user) {
        return user.toByteArray();
    }

    private static UserProto.User decode(byte[] bytes) throws InvalidProtocolBufferException {
        return UserProto.User.parseFrom(bytes);
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        Builder builder = User.newBuilder();
        builder.setName("szj");
        builder.setUid(1);
        User user = builder.build();
        User user_ = decode(encode(user));
        // true
        System.out.println(user.equals(user_));
    }

}
