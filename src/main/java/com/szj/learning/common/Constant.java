package com.szj.learning.common;

import java.net.InetSocketAddress;

public interface Constant {

    int SERVER_PORT = 8080;

    String LOCAL_HOST = "127.0.0.1";

    InetSocketAddress LOCAL_ADDRESS = new InetSocketAddress(LOCAL_HOST, SERVER_PORT);

    String CLIENT_SEND_MSG = "客户端发送的消息\t";

    String SERVER_RSP_MSG = "服务端响应的消息\t";

    String SOCKET_DISCONNECT = "通信断开";

    String CLIENT_OFF = "客户端下线";

    String SERVER_OFF = "服务端下线";

    String STR_END = System.getProperty("line.separator");

}
