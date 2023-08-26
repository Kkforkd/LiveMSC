package me.LiveSongs;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class LiveAPI implements HttpHandler {

    public static String lastDanmu = "";
    public static String user = "";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JsonObject result = new JsonObject();
        switch (httpExchange.getRequestURI().getPath()){
            case "/help":
                result.addProperty("maker", "Kkforkd");
                sendResponse(httpExchange, 0, result.getAsString());
                break;
            case "/get_latest_danmu":
                result.addProperty("text", lastDanmu);
                result.addProperty("sender", user);
                sendResponse(httpExchange, 0, result.getAsString());
                break;
            case "/get_playing_music":
                result.addProperty("music", LiveSongs.GUI.playingMusic);
                result.addProperty("player", LiveSongs.GUI.playerName);
                break;
            default:

        }
    }
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }
}
