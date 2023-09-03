package com.szj.learning.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.szj.learning.bio.BIOConstant.*;

public class BIOClient {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket(LOCAL_HOST, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("客户端发送了一条消息");
            String rsp = in.readLine();
            System.out.printf("客户端收到服务端响应： " + rsp);
        }
    }

}
