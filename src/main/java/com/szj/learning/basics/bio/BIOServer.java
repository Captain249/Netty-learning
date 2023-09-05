package com.szj.learning.basics.bio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.szj.learning.common.Constant.SERVER_PORT;

public class BIOServer {

    public static void main(String[] args) throws IOException {
        fakeSyncIo();
    }

    // 简单的BIO通信
    public static void simpleBIOServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new BIOServerHandler(socket)).start();
            }
        }
    }

    // 伪异步IO
    public static void fakeSyncIo() throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new BIOServerHandler(socket));
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class BIOServerHandler implements Runnable {

        private Socket socket;

        @Override
        public void run() {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream())); PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true)) {
                String body;
                while (true) {
                    body = in.readLine();
                    if (body == null) {
                        break;
                    }
                    System.out.printf("服务端收到请求: " + body);
                    out.println("服务端的响应");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
