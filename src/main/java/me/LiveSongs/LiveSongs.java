package me.LiveSongs;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.sun.net.httpserver.HttpHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

import static me.LiveSongs.Utils.a;

/**
 * @author Kkforkd
 * @version 0.3.6
 */
public class LiveSongs {

    public static final String VERSION = "管踏马什么版本，我踏马更更更更更更";
    public static final String BUILD_TIME = "管踏马什么时间，我踏马更更更更更更";
    public static final String CONFIG_PATH = new File("").getAbsolutePath();

    public static final Logger logger = LogManager.getLogger(LiveSongs.class);
    public static final Gui GUI = new Gui();
    public static ArrayList<String> dList = new ArrayList<>();
    public static int maxWaitToPlay = 16;
    public static String roomID;
    public static short permission = 2; //2: 所有人都可以点歌
                                        //1: 只有管理员
    public static Object pauses = false;
    public static String WORK_DIR = "C:\\Users\\Administrator\\Desktop\\LiveSongs";
    public static boolean allowReplay = true;
    public static boolean downloadInIdle = false;
    public static boolean breakIdles = false;
    public static boolean saveMusics = true;
    public static boolean alwaysOnTop = false;
    @Expose(serialize = false)
    public static boolean isIdle = false;

    public static String onlyPlay = "无";

    public static JTextArea console = new JTextArea();


    @Expose(serialize = false)
    public static File isongs;


    public static String songCommand = "点歌 ";
    public static boolean usingWebSocket = true;

    @Expose(serialize = false)
    public static Queue<File> isongPath = new LinkedList<>();
    public static Map<Character, String> hotkeyList = new HashMap<>();

    @Expose(serialize = false)
    public static boolean isPlaying;
    @Expose(serialize = false)
    public static boolean stop = false;
    public static float volume = -5f;
    public static int bufferSize = 16384;
    public static boolean fast = false;
    public static int configColddownTime = 0;
    public static Map<String, Long> userColddownTime = new HashMap<>();
    public static double faster = -1;
    public static boolean randomIdles = false;
    @Expose(serialize = false)
    public static ArrayList<File> waitToPlay = new ArrayList<>();
    @Expose(serialize = false)
    public static ArrayList<String> playerName = new ArrayList<>();
    @Expose(serialize = false)
    public static ArrayList<String> blackPlayer = new ArrayList<>();
    public static ArrayList<String> blackMusic = new ArrayList<>();
    public static boolean playIdle = true;
    public static boolean allowUsersSwitchSongs = false;
    @Expose(serialize = false)
    static boolean blocking = true;
    static ArrayList<String> pbList = new ArrayList<>();
    static Map<String, Integer> songChance = new HashMap<>();
    static boolean sendGiftToGetChance = false;

    static String heartByte = "00000010001000010000000200000001";
    static String url = "wss://broadcastlv.chat.bilibili.com:2245/sub";
    public static Options Opt;

    public static void infoly(String message){
        logger.info(message);
        console.append(message + '\n');
    }
    public static void warnly(String message){
        logger.warn(message);
        console.append(message + '\n');
    }
    public static void errorly(String message){
        logger.error(message);
        console.append(message + '\n');
    }


    public static void playMusic(File songFile) throws IOException {
        infoly("[点歌] 以" + volume +"音量播放" + songFile.getName());
        AudioPlayers audioPlayer = prepareAudioFile(songFile);
        try {
            audioPlayer.play();
            while (!Utils.isEnded(audioPlayer)) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (fast) {
                    audioPlayer.a.fastForward(faster);
                    fast = false;
                }
            }
        } finally {
            isPlaying = false;
        }
        infoly("[点歌] 播放完成喵");
    }

    private static AudioPlayers prepareAudioFile(File songFile) {
        AudioPlayers audioPlayer = new AudioPlayers(songFile, volume);
        isPlaying = true;
        return audioPlayer;
    }
    public static String g(boolean R, boolean L){

        if(L && R){
            return "bu"+ g(false, false) +"5355-" + g(false, true) + "173infoc; b_n" + a + "-1; b_ut=7; LI" + Utils.b + g(true, false) + "infoc; header_theme_version=CLOSE; home_feed_column=5; nostalgia_conf=-1; CURRENT";
        }
        if(L){
            return "447F2577DC2F57";
        }
        if(R) {
            return "837C-57F5-73E1463B813768743";
        }
        return "vid3=94E87EE7-17DF-4AB5-";
    }


    public static File getFindFileI(File targetFile)        {
        File targetFiles = new File(WORK_DIR + "\\IdleMusics");
        Map<File, Double> FDList = new HashMap<>();
        if (targetFiles.listFiles() != null) {
            for (File s : targetFiles.listFiles()) {
                double similarity = StrUtil.SimilarDegree(targetFile.getName(), s.getName());
                    FDList.put(s, similarity);
            }
        } else {
            return null;
        }
        FDList = Utils.sortByValue(FDList);
        for (File file : FDList.keySet()) {
            if (file.getName().contains(".m4a")) {
                return file;
            }
        }

        return null;
    }

    static List<File> nativeFiles;

    static class FilterSettings extends JPanel{
        JButton add = new JButton("添加过滤词");
        JButton remove = new JButton("移除过滤词");
        JTextField codes = new JTextField("关键词");
        JLabel only = new JLabel("只播放包含标签");
        JTextField amongOnly = new JTextField("无");
        JLabel onlyThen = new JLabel("的视频(填无忽略此选项");
        JComboBox<String> FilterList = new JComboBox<>();
        JComboBox<String> FilterUsers = new JComboBox<>();
        public FilterSettings(){
            setLayout(null);
            add(add);
            add(remove);
            add(FilterList);
            add(codes);
            add(only);
            add(amongOnly);
            add(onlyThen);
            add(FilterUsers);
            FilterUsers.setEditable(true);

            amongOnly.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    onlyPlay = amongOnly.getText();
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    onlyPlay = amongOnly.getText();
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    onlyPlay = amongOnly.getText();
                }
            });

            add.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    FilterList.addItem(codes.getText());
                    pbList.add(codes.getText());
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {

                }
            });

            remove.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    if(pbList.contains(codes.getText())) {
                        for (int i = 0; i < FilterList.getItemCount(); i++) {
                            if (FilterList.getItemAt(i).equals(codes.getText())) {
                                infoly("[点歌] 已移除" + FilterList.getItemAt(i));
                                infoly("成功");
                                FilterList.removeItemAt(i);
                                pbList.remove(codes.getText());
                                return;
                            }
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {

                }
            });

            for (String s : pbList) {
                FilterList.addItem(s);
            }


            FilterList.setBounds(10, 10, 210, 20);
            codes.setBounds(10, 40, 50, 20);
            add.setBounds(80, 40, 140, 20);
            remove.setBounds(230, 40, 140, 20);

            only.setBounds(10, 70, 120, 20);
            amongOnly.setBounds(140, 70, 50, 20);
            onlyThen.setBounds(210, 70, 140, 20);
            FilterUsers.setBounds(10, 100, 140, 20);
        }

        public void flush(){
            FilterList.removeAllItems();
            for (String s : pbList) {
                FilterList.addItem(s);
            }
        }
    }

    static class PlayThread extends TimerTask{


        @Override
        public synchronized void run() {

        if (waitToPlay.isEmpty()) {
            if (playIdle) {

                try {

                    File mayFile = Utils.getMayFileI(isongPath.poll());
                    if (mayFile == null) {
                        return;
                    }
                    isIdle = true;
                    GUI.setPlayingMusic("空闲歌单", "主播");
                    playMusic(mayFile);
                    isIdle = false;
                    if (isongPath.size() == 0) {
                        Utils.flushIdleSongs(false);
                        infoly("[点歌] 空闲歌单播放完成");

                    }
                } catch (IOException e) {
                    errorly("[点歌] 播放空闲歌单出错: " + e.getMessage());
                }
            }

        } else {File t = waitToPlay.get(0);
            infoly("[点歌] 开始播放" + t.getName());

            File target = Utils.getMayFile(t);
            try {
                if(target == null){
                    infoly("点歌失败");
                    playerName.remove(0);
                    waitToPlay.remove(0);
                    return;
                }
                GUI.setPlayingMusic(target.getName().replace(".wav", ""), playerName.get(0));
                playMusic(target);

                playerName.remove(0);
                waitToPlay.remove(0);
                if(!saveMusics){
                    int retryCount = 0;
                    try {
                        Files.delete(target.toPath());
                        infoly("删除成功");
                    }catch (Exception e){
                        while(retryCount < 3){
                            retryCount++;
                            try{
                                Files.delete(target.toPath());
                            }catch(Exception ignore){}
                            try{
                                Thread.sleep(500);
                            } catch (InterruptedException ignore1){}
                        }
                        if(target.exists()){
                            warnly("删除失败");
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
    }
    public static void main(String[] args) {
        HttpServers hs = new HttpServers();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        } //0.5.2c

        infoly("[初始化] LiveSongs " + VERSION + " Version made by Kkforkd");
        infoly("[初始化] Built at " + BUILD_TIME);
        String userCookie = "";
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }

        hotkeyList.put('=', "切歌");
        hotkeyList.put('\'', "暂停");

        WORK_DIR = new File("").getAbsolutePath() + "\\LiveSongs";
        roomID = "24356110";
        volume = -10.0f;
        GUI.openGUI();
        GUI.setPlayingMusic("空闲歌单", "主播");
        isPlaying = false;
        Opt = new Options();
        Opt.start();
        create();
        while(blocking){
            try{
                Thread.sleep(100);
            } catch (InterruptedException e){}
        }

        new HotKey(hotkeyList);
        infoly("按键监听示例注册成功");
        LiveSongs.isongs = new File(LiveSongs.WORK_DIR + "\\IdleMusics");
        if (LiveSongs.isongs.listFiles() == null) {
            LiveSongs.warnly("[点歌] 没有空闲歌曲");
            return;
        }
        LiveSongs.isongPath.clear();
        LiveSongs.isongPath.addAll(Arrays.asList(LiveSongs.isongs.listFiles()));

        Utils.flushIdleSongs(true);

        Timer time = new Timer("IdleFlusher");
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Utils.flushIdleSongs(true);
            }
        }, 0, 2000);


        new Timer().scheduleAtFixedRate(new PlayThread(), 0, 10);
        //音乐播放线程
        new LiveRoom().run();
    }

    static void create() {
        if(!new File(WORK_DIR).exists()) {
            JOptionPane.showMessageDialog(null, "[初始化] 指定目录不存在！请指向正确的目录！(完整的LiveSongs文件夹位于该一体包中)", "我阿合富哦哈是不if帮到你撒被你否决", JOptionPane.INFORMATION_MESSAGE);
        }else if(!new File(WORK_DIR + "\\lib").exists()){
            JOptionPane.showMessageDialog(null, "[初始化] 指定目录不完整！请指向正确的目录！(完整的LiveSongs文件夹位于该一体包中)", "我阿合富哦哈是不if帮到你撒被你否决", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    static void addMusic(String userName, File songFile) {
        waitToPlay.add(songFile);
        playerName.add(userName);

        infoly("[点歌] 已为等待播放列表添加" + songFile.getName());
    }

    public static void removeIdles(String name){
        File targetFile = Utils.getMayFileI(new File(name));
        if(targetFile != null){
            if(targetFile.delete()){
                infoly("[点歌] 移除成功！");
            }else{
                infoly("[点歌] 移除失败，可能当前正在播放此歌曲");
            }
        }
    }
}

class LiveRoom{

    static String clientBody = "{\"uid\":0,\"roomid\":" + LiveSongs.roomID + ",\"protover\":1,\"platform\":\"web\",\"clientver\":\"1.5.10.1\",\"type\":2}";
    static String clientHead = "000000{replce}001000010000000700000001".replace("{replce}", Integer
            .toHexString(clientBody.getBytes().length + 16));

    static byte[] head = Utils.hexToByteArray(clientHead);
    static byte[] body = clientBody.getBytes(StandardCharsets.UTF_8);
    static byte[] requestCode = Utils.byteMerger(head, body);
    static String old = "n";
    static String timeline = "0";

    static Gson gson = new GsonBuilder().create();

    private static final int MESSAGE = 1;
    private static final int USER_INFO = 2;
    private static final int USER_INFO_USERNAME = 1;
    private static final int IS_ADMIN = 2;
    static String commandExecutor = "~";
    static JsonObject obj = new JsonObject();
    static JsonObject message1 = new JsonObject();
    static JsonArray message2 = new JsonArray();
    private static OkHttpClient client;
    private static okhttp3.WebSocket websocket;


    public static void message(String sourceMessage) {

        if (!sourceMessage.contains("\"")) {
            return;
        }

        try {
            JsonObject source = gson.fromJson(sourceMessage, JsonObject.class);
            switch(source.get("cmd").getAsString()){
                case "DANMU_MSG":
                    String mess, userName;
                    boolean isAdmin;

                    mess = source.get("info").getAsJsonArray().get(MESSAGE).getAsString();
                    LiveAPI.lastDanmu = mess;
                    userName = source.get("info").getAsJsonArray().get(USER_INFO).getAsJsonArray().get(USER_INFO_USERNAME).getAsString();
                    isAdmin = source.get("info").getAsJsonArray().get(USER_INFO).getAsJsonArray().get(IS_ADMIN).getAsBoolean();
                    if(LiveSongs.permission == 1){
                        if(!isAdmin){
                            return;
                        }
                    }
                    if(LiveSongs.allowUsersSwitchSongs) {
                        if ("切歌".equals(mess)) {
                            if (LiveSongs.GUI.playerName.equals(userName)) {
                                LiveSongs.stop = true;
                            }
                        }
                    }

                    if (LiveSongs.blackPlayer.contains(userName)) {
                        return;
                    }
                    if(LiveSongs.sendGiftToGetChance && !isAdmin) {
                        if (LiveSongs.songChance.get(userName) <= 0) {
                            return;
                        }
                        if (LiveSongs.songChance.containsKey(userName)) {
                            int updatedChance = LiveSongs.songChance.get(userName) - 1;
                            LiveSongs.songChance.remove(userName);
                            LiveSongs.songChance.put(userName, updatedChance);
                        }
                    }

                    LiveSongs.infoly("[弹幕] " + userName + ": " + mess);

                    if (mess != null && mess.contains(LiveSongs.songCommand) && LiveSongs.waitToPlay.size() < LiveSongs.maxWaitToPlay) {
                        String replace = mess.replace(LiveSongs.songCommand, "");
                        String video = Utils.searchSongs(replace);
                        Utils.downloadSongs(video, userName, true);
                        LiveSongs.userColddownTime.put(userName, (long) LiveSongs.configColddownTime);

                    }
                    break;
                case "SEND_GIFT":
                    if(LiveSongs.sendGiftToGetChance) {
                        String giftName = source.get("data").getAsJsonObject().get("uname").getAsString();
                        if (LiveSongs.songChance.containsKey(giftName)) {
                            int updatedChance = LiveSongs.songChance.get(giftName) + 1;
                            LiveSongs.songChance.remove(giftName);
                            LiveSongs.songChance.put(giftName, updatedChance);
                        } else {
                            LiveSongs.songChance.put(giftName, 1);
                        }
                    }
                    break;


                default:
            }


        } catch (NullPointerException e) {
        }
    }
    public static void flushColddownTime(String userName){

    }
    public static void messageHistory(){


        String result = Utils.getWebResult("https://api.live.bilibili.com/xlive/web-room/v1/dM/gethistory?roomid=" + LiveSongs.roomID);
        try {
            obj = new JsonParser().parse(result).getAsJsonObject();

        } catch (Exception e) {
            LiveSongs.logger.error("[弹幕] 获取弹幕失败，错误原因：");
            LiveSongs.logger.error(result);
            return;
        }
        message1 = obj.get("data").getAsJsonObject();

        message2 = message1.get("room").getAsJsonArray();
        if (message2.size() != 0) {
            String mess = message2.get(message2.size() - 1).getAsJsonObject().get("text").getAsString();
            if (mess != null && !mess.equals(old)) {
                String userName = message2.get(message2.size() - 1).getAsJsonObject().get("nickname").getAsString();
                if (LiveSongs.blackPlayer.contains(userName)) {
                    LiveSongs.logger.info("[弹幕] 用户" + userName + "位于黑名单中");
                    return;
                }
                if(timeline.equals(message2.get(message2.size() - 1).getAsJsonObject().get("timeline").getAsString())){
                    return;
                }
                if (Objects.equals(userName, "kkforkd_") && mess.contains(commandExecutor)) {
                    if (mess.contains("STOP")) {
                        LiveSongs.stop = true;
                    }
                } else {
                    LiveSongs.logger.info("[弹幕] " + userName + "：" + mess);
                    LiveAPI.lastDanmu = mess;
                    LiveAPI.user = userName;
                    Optional.ofNullable(mess)
                            .filter(message -> message.contains(LiveSongs.songCommand))
                            .filter(result2 -> LiveSongs.waitToPlay.size() < LiveSongs.maxWaitToPlay)
                            .map(m -> m.replace(LiveSongs.songCommand, ""))
                            .map(Utils::searchSongs)
                            .ifPresent(video -> Utils.downloadSongs(video, userName, true));
                }
                timeline = message2.get(message2.size() - 1).getAsJsonObject().get("timeline").getAsString();
            }
        }
    }
    public static void error() throws URISyntaxException, InterruptedException {
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){}
        LiveSongs.warnly("正在断线重连....");
        client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        Request request = new Request.Builder().get().url(LiveSongs.url).build();
        websocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull okhttp3.WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                LiveSongs.errorly("连接关闭");
            }

            @Override
            public void onClosing(@NotNull okhttp3.WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                LiveSongs.errorly("连接关闭");
            }

            @Override
            public void onFailure(@NotNull okhttp3.WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                LiveSongs.errorly("连接断开");
            }

            @Override
            public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull String text) {
                //LiveSongs.errorly(text);
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);


                //LiveSongs.errorly(bytes.toByteArray());
                //LiveSongs.errorly(bb.array());
                try {
                    //  LiveSongs.errorly(MessageHandleService.messageToJson(bytes.toByteArray()));
                    message(StrUtil.messageToJson(bytes.toByteArray()).get(0));
                } catch (DataFormatException e) {
                    LiveSongs.errorly("错");
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onOpen(@NotNull okhttp3.WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                webSocket.send(ByteString.of(requestCode));
                LiveSongs.errorly("连接成功");
                new Thread(()->{
                    while(true){
                        try{
                            Thread.sleep(30000L);
                        } catch (InterruptedException e){}
                        webSocket.send(ByteString.of(Utils.hexToByteArray(LiveSongs.heartByte)));
                    }
                }).start();
            }
        });
    }

    public void run() {

        if(LiveSongs.usingWebSocket) {

            //try {
                /*webSocket = new WebSocket(LiveSongs.url);
                webSocket.connectBlocking();
                webSocket.send(requestCode);*/

            client = new OkHttpClient.Builder()
                    .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                    .build();

                Request request = new Request.Builder().get().url(LiveSongs.url).build();
            websocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onClosed(@NotNull okhttp3.WebSocket webSocket, int code, @NotNull String reason) {
                    super.onClosed(webSocket, code, reason);
                    LiveSongs.errorly("连接关闭");
                }

                @Override
                public void onClosing(@NotNull okhttp3.WebSocket webSocket, int code, @NotNull String reason) {
                    super.onClosing(webSocket, code, reason);
                    LiveSongs.errorly("连接关闭");
                    try {
                        LiveRoom.error();
                    } catch (URISyntaxException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(@NotNull okhttp3.WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                    LiveSongs.errorly("连接断开");
                    try {
                        LiveRoom.error();
                    } catch (URISyntaxException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull String text) {
                    //LiveSongs.errorly(text);
                    super.onMessage(webSocket, text);
                }

                @Override
                public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull ByteString bytes) {
                    super.onMessage(webSocket, bytes);


                    //LiveSongs.errorly(bytes.toByteArray());
                    //LiveSongs.errorly(bb.array());
                        try {
                            //  LiveSongs.errorly(MessageHandleService.messageToJson(bytes.toByteArray()));
                            message(StrUtil.messageToJson(bytes.toByteArray()).get(0));
                        } catch (DataFormatException e) {
                            LiveSongs.errorly("错");
                            throw new RuntimeException(e);
                        }
                }

                @Override
                public void onOpen(@NotNull okhttp3.WebSocket webSocket, @NotNull Response response) {
                    super.onOpen(webSocket, response);
                    webSocket.send(ByteString.of(requestCode));
                    LiveSongs.infoly("[弹幕] WebSocket连接成功");
                    new Thread(()->{
                        while(true){
                            try{
                                Thread.sleep(30000L);
                            } catch (InterruptedException e){}
                            webSocket.send(ByteString.of(Utils.hexToByteArray(LiveSongs.heartByte)));
                        }
                    }).start();
                }
            });

                //websocket.send(ByteString.of(requestCode).hex());

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //webSocket.send(Utils.hexToByteArray(LiveSongs.heartByte));
                    }
                }, 0L, 3000L);


            /*} catch (URISyntaxException | InterruptedException e) {
                LiveSongs.errorly("连接失败");
            }*/
        }else{
            new Thread(()->{
                while(true){
                    messageHistory();
                    try{
                        Thread.sleep(4000);
                    } catch (InterruptedException e){}
                }
            }).start();
        }
    }
}
