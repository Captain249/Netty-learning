package com.szj.learning.netty.custom.codec;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/12 11:44 上午
 * @Description
 */
@Data
public class Header {

    private final int crcCode = 0xabef0101;// 版本号
    private int length;// 消息长度
    private long sessionId;// 会话ID
    private byte type;// 消息类型
    private byte priority;// 消息优先级
    private final Map<String, Object> attachment = new HashMap<>();

    public Header(int length, long sessionId, byte type, byte priority) {
        this.length = length;
        this.sessionId = sessionId;
        this.type = type;
        this.priority = priority;
    }

}
