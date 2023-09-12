package com.szj.learning.netty.custom.codec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/12 11:41 上午
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private Header header; // 消息头

    private Object body; // 消息体

}
