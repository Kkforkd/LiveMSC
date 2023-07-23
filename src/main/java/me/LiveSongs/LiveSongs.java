package me.LiveSongs;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Timer;
import java.util.*;

import static me.LiveSongs.LiveSongs.*;

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

    @Expose(serialize = false)
    public static List<File> isongPath;

    @Expose(serialize = false)
    public static boolean isPlaying;
    @Expose(serialize = false)
    public static boolean stop = false;
    public static float volume = -5f;
    public static int bufferSize = 16384;
    public static boolean fast = false;
    public static int colddownTime = 0;
    public static int configColddownTime = 0;
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
    @Expose(serialize = false)
    static boolean blocking = true;
    static ArrayList<String> pbList = new ArrayList<>();
    static Map<String, Integer> songChance = new HashMap<>();
    static boolean sendGiftToGetChance = false;

    static String heartByte = "00000010001000010000000200000001";
    static String url = "wss://broadcastlv.chat.bilibili.com:2245/sub";

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
    public static void flushColdTime(){
        colddownTime--;
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

        int i = 0;

        @Override
        public void run() {

        if (waitToPlay.isEmpty()) {
            if (playIdle) {

                try {
                    if (i >= isongPath.size()) {
                        i = 0;
                        Utils.flushIdleSongs(false);
                        infoly("[点歌] 空闲歌单播放完成");
                    }
                    File mayFile = Utils.getMayFileI(isongPath.get(i));
                    if (mayFile == null) {
                        i++;
                        return;
                    }
                    isIdle = true;
                    GUI.setPlayingMusic("空闲歌单", "主播");
                    playMusic(mayFile);
                    isIdle = false;
                    i++;
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

        infoly("[初始化] LiveSongs " + VERSION + " Version made by Kkforkd");
        infoly("[初始化] Built at " + BUILD_TIME);
        

        infoly("[初始化] cookie刷新成功");

        WORK_DIR = new File("").getAbsolutePath() + "\\LiveSongs";
        roomID = "24356110";
        volume = -10.0f;

        GUI.openGUI();
        GUI.setPlayingMusic("空闲歌单", "主播");
        isPlaying = false;
        new Options().start();
        create();
        while(blocking){
            try{
                Thread.sleep(100);
            } catch (InterruptedException e){}
        }


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

        Timer timer = new Timer("GetMessage");

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

    String clientBody = "{\"uid\":0,\"roomid\":" + roomID + ",\"protover\":1,\"platform\":\"web\",\"clientver\":\"1.5.10.1\",\"type\":2}";
    String clientHead = "000000{replce}001000010000000700000001".replace("{replce}", Integer
            .toHexString(clientBody.getBytes().length + 16));

    byte[] head = Utils.hexToByteArray(clientHead);
    byte[] body = clientBody.getBytes(StandardCharsets.UTF_8);
    byte[] requestCode = Utils.byteMerger(head, body);


    String commandExecutor = "~";
    String old = "n";
    JsonObject obj = new JsonObject();
    String timeline = "0";
    JsonObject lastMessage = new JsonObject();

    static Gson gson = new GsonBuilder().create();
    WebSocket webSocket;


    private static final int MESSAGE = 1;
    private static final int USER_INFO = 2;
    private static final int USER_INFO_USERNAME = 1;
    private static final int IS_ADMIN = 2;

    public static void message(String sourceMessage) {

        if (!sourceMessage.contains("\"")) {
            return;
        }
        if (colddownTime > 0) {
            return;
        }

        try {
            JsonObject source = gson.fromJson(sourceMessage, JsonObject.class);
            switch(source.get("cmd").getAsString()){
                case "DANMU_MSG":
                    String mess, userName;
                    boolean isAdmin;

                    mess = source.get("info").getAsJsonArray().get(MESSAGE).getAsString();
                    userName = source.get("info").getAsJsonArray().get(USER_INFO).getAsJsonArray().get(USER_INFO_USERNAME).getAsString();
                    isAdmin = source.get("info").getAsJsonArray().get(USER_INFO).getAsJsonArray().get(IS_ADMIN).getAsBoolean();
                    if(permission == 1){
                        if(!isAdmin){
                            return;
                        }
                    }

                    if (blackPlayer.contains(userName)) {
                        return;
                    }
                    if(sendGiftToGetChance && !isAdmin) {
                        if (songChance.get(userName) <= 0) {
                            return;
                        }
                        if (songChance.containsKey(userName)) {
                            int updatedChance = songChance.get(userName) - 1;
                            songChance.remove(userName);
                            songChance.put(userName, updatedChance);
                        }
                    }

                    infoly("[弹幕] " + userName + ": " + mess);
                    Optional.ofNullable(mess)
                            .filter(message -> message.contains(songCommand))
                            .filter(result -> waitToPlay.size() < maxWaitToPlay)
                            .map(m -> m.replace(songCommand, ""))
                            .map(Utils::searchSongs)
                            .ifPresent(video -> Utils.downloadSongs(video, userName, true));
                    break;
                case "SEND_GIFT":
                    if(sendGiftToGetChance) {
                        String giftName = source.get("data").getAsJsonObject().get("uname").getAsString();
                        if (songChance.containsKey(giftName)) {
                            int updatedChance = songChance.get(giftName) + 1;
                            songChance.remove(giftName);
                            songChance.put(giftName, updatedChance);
                        } else {
                            songChance.put(giftName, 1);
                        }
                    }
                    break;


                default:
                    return;
            }


        } catch (NullPointerException e) {
        }
    }
    public void run() {

        try {
            webSocket = new WebSocket(url);
            webSocket.connectBlocking();
            webSocket.send(requestCode);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    webSocket.send(Utils.hexToByteArray(heartByte));
                }
            }, 0L, 30000L);



        } catch (URISyntaxException | InterruptedException e) {
            errorly("连接失败");
        }

        old = timeline;
    }
}