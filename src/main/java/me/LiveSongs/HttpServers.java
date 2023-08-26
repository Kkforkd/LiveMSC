package me.LiveSongs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServers{
    public HttpServers(){
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 创建处理请求的上下文路径为"/"的处理器
        server.createContext("/", new LiveAPI());

        // 启动服务器
        server.start();

        System.out.println("服务器已启动，监听端口号：8000");
    }

}