package com.szj.learning.netty.serialization;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/6 7:20 下午
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer uid;

    public byte[] codec() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] bytesForName = this.name.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(bytesForName.length);// 对象型先给长度再给值
        buffer.put(bytesForName);
        buffer.putInt(this.uid);// 基础类型直接给对应Put方法赋值

        buffer.flip();
        byte[] bytesForOut = new byte[buffer.remaining()];
        buffer.get(bytesForOut);
        return bytesForOut;
    }
}
