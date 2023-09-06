package com.szj.learning.netty.serialization.jdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.szj.learning.netty.serialization.User;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/6 7:22 下午
 * @Description
 */
public class JDKSerialization {

    public static void main(String[] args) throws IOException {
        User user = new User();
        user.setName("沈卓钧");
        user.setUid(1001);
        // JDK序列化 字节长度大 时间慢 不推荐使用
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(user);
        os.flush();
        os.close();
        byte[] bytesForJDK = bos.toByteArray();
        System.out.println("JDK的序列化，字节数组长度" + bytesForJDK.length);
        byte[] bytesForByteBuffer = user.codec();
        System.out.println("ByteBuffer序列化，字节数组长度" + bytesForByteBuffer.length);
    }

}
