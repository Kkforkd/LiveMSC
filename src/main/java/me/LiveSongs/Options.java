package me.LiveSongs;

import com.google.gson.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import static me.LiveSongs.LiveSongs.*;

public class Options {
    final JTextField jtf = new JTextField();
    final JTextField volumeT = new JTextField();
    final JTextField jtfRunning = new JTextField();
    final JCheckBox allowRep = new JCheckBox("允许重复点歌");
    final JCheckBox CbreakIdle = new JCheckBox("打断空闲歌单");
    final JCheckBox saveMusic = new JCheckBox("保存下载的音乐");
    final JTextField addIdlest = new JTextField("填写歌曲名");
    final JButton addIdles = new JButton("添加空闲歌曲");
    final JButton hMusic = new JButton("手动点歌");
    final JTextField hm = new JTextField("填写歌曲名");
    final JCheckBox randomIdle = new JCheckBox("随机播放空闲歌单");
    final JButton stops = new JButton("手动执行切歌");
    final JButton button = new JButton("开启监听弹幕");
    final JTextField dm1 = new JTextField("无弹幕");
    final JTextField dm2 = new JTextField("无弹幕");
    final JTextField dm3 = new JTextField("无弹幕");
    final JSlider volumesJSlider = new JSlider(-25, 2);

    final JLabel jtfLabel = new JLabel("直播间ID：");
    final JButton pause = new JButton("暂停");

    final JLabel volumeLabel = new JLabel("音量:");

    final JLabel running = new JLabel("LiveSongs目录");

    final JTextField remove = new JTextField("填写歌曲名");
    final JButton removeButton = new JButton("移除空闲歌曲");

    final JButton loadConfig = new JButton("加载配置");
    final JButton saveConfig = new JButton("保存配置");
    JCheckBox permissionSettings = new JCheckBox("只许房管点歌");
    public static boolean p = false;
    LiveSongs.FilterSettings fs = new LiveSongs.FilterSettings();
    JTextField format = new JTextField(GUI.getFormat());
    JTextField bufferSetter = new JTextField("缓冲区大小");
    JTextField MaxSongs = new JTextField("16384");
    JTextField songCommandSettings = new JTextField("点歌 ");
    JLabel labelOfColddown = new JLabel("点歌冷却时间(秒)");
    JTextField coldTime = new JTextField("0");
    JCheckBox downloadInIdleSettings = new JCheckBox("用户点的歌下载到空闲歌单里");
    JComboBox<String> musicBlack = new JComboBox<>();
    JComboBox<String> playerBlack = new JComboBox<>();
    JButton addPlayerBlack = new JButton("添加黑名单点歌人");
    JButton addMusicBlack = new JButton("添加黑名单歌曲");
    JTextField playerBlackAdd = new JTextField("黑名单点歌人");
    JTextField musicBlackAdd = new JTextField("黑名单BV号");
    JButton removeBlackPlayer = new JButton("移除");
    JButton removeBlackMusic = new JButton("移除");
    JCheckBox giftToSong = new JCheckBox("送礼物获取点歌机会");

    public void refresh() {
        jtf.setText(roomID);
        volumeT.setText(String.valueOf(volume));
        volumesJSlider.setValue((int) volume);
        jtfRunning.setText(WORK_DIR);
        allowRep.setSelected(allowReplay);
        CbreakIdle.setSelected(breakIdles);
        randomIdle.setSelected(randomIdles);
        bufferSetter.setText(String.valueOf(bufferSize));
        saveMusic.setSelected(saveMusics);
        permissionSettings.setSelected(permission == 1);
        MaxSongs.setText(String.valueOf(maxWaitToPlay));
        songCommandSettings.setText(songCommand);
        coldTime.setText(String.valueOf(colddownTime));
        downloadInIdleSettings.setSelected(downloadInIdle);

        playerBlack.removeAllItems();
        for(String s : blackPlayer){
            playerBlack.addItem(s);
        }

        musicBlack.removeAllItems();
        for(String s : blackMusic){
            musicBlack.addItem(s);
        }
    }

    public void start() {
        JFrame frame = new JFrame("LiveMSC 0.5.0a made by Kkforkd");
        JPanel panel = new JPanel();
        JPanel panel2 = new JPanel(); //其他
        JPanel songs = new JPanel(); //点歌
        JPanel gui = new JPanel(); //界面

        JTabbedPane mainPanel = new JTabbedPane();

        panel.setName("主要");

        panel2.setName("过滤");

        mainPanel.addTab("基础", panel);
        mainPanel.addTab("其他", panel2);
        mainPanel.addTab("过滤", fs);
        mainPanel.addTab("点歌", songs);
        mainPanel.addTab("界面", gui);

        panel.setLayout(null);
        songs.setLayout(null);
        gui.setLayout(null);
        frame.setContentPane(mainPanel);

        Gson gson = new GsonBuilder()
                .create();
        {
            jtf.setText(roomID);

            volumesJSlider.setValue((int) (volume));

            volumesJSlider.addChangeListener(e -> {
                try {
                    volume = volumesJSlider.getValue();
                } catch (NumberFormatException ignored) {
                }
            });


            panel.add(jtfLabel);
            panel.add(jtf);

            panel.add(volumeLabel);
            panel.add(volumesJSlider);


            jtfRunning.setText(WORK_DIR);

            panel.add(running);
            panel.add(jtfRunning);

            allowRep.setSelected(true);


            CbreakIdle.setSelected(false);


            panel.add(remove);


            dm1.setEditable(false);
            dm2.setEditable(false);
            dm3.setEditable(false);

            panel.add(dm1);
            panel.add(dm2);
            panel.add(dm3);

            panel.add(randomIdle);

            AtomicReference<ArrayList<String>> thrArray = new AtomicReference<>(new ArrayList<>());

            java.util.Timer timer = new java.util.Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    thrArray.set(dList);
                }
            }, 0, 5000);

            java.util.Timer flushMessage = new Timer();

            flushMessage.scheduleAtFixedRate(new TimerTask() {
                String olds = "无弹幕";
                String old2 = olds;
                @Override
                public void run() {
                    if (thrArray.get().isEmpty()) {
                        return;
                    }
                    if (!thrArray.get().get(thrArray.get().size() - 1).equals(olds)) {
                        dm1.setText(thrArray.get().get(thrArray.get().size() - 1));
                        dm2.setText(olds);
                        dm1.repaint();
                        dm2.repaint();
                        dm3.setText(old2);
                        dm3.repaint();
                        old2 = olds;
                        olds = thrArray.get().get(thrArray.get().size() - 1);
                    }
                }
            }, 0, 500);


            JCheckBox playIdles = new JCheckBox("是否播放空闲歌单");
            playIdles.setSelected(true);


            panel.add(playIdles);

            button.addActionListener(e -> {

                infoly("[初始化] 程序启动于" + WORK_DIR);
                blocking = false;
                infoly("[点歌] 正在启动监听");
                roomID = jtf.getText();
                WORK_DIR = jtfRunning.getText();
                breakIdles = CbreakIdle.isSelected();
                allowReplay = allowRep.isSelected();
                randomIdles = randomIdle.isSelected();
                playIdle = playIdles.isSelected();
                try {
                    bufferSize = Integer.parseInt(bufferSetter.getText());
                } catch(Exception ignore){ }
                playIdles.setEnabled(false);
                try {
                    volume = Float.parseFloat(volumeT.getText());
                }catch (NumberFormatException ignore){}
                loadConfig.setEnabled(false);
                randomIdle.setEnabled(false);
                CbreakIdle.setEnabled(false);
                allowRep.setEnabled(false);
                jtfRunning.setEnabled(false);
                button.setEnabled(false);
                jtf.setEnabled(false);
                bufferSetter.setEnabled(false);

                if(!new File(WORK_DIR).exists()){
                    warnly("[初始化] " + WORK_DIR + "目录不存在！");
                    create();
                }

            });


            jtfLabel.setBounds(10, 10, 65, 20); //直播间ID
            jtf.setBounds(70, 10, 65, 20); //直播间id 输入框

            volumeLabel.setBounds(10, 40, 45, 20); //音量：
            volumesJSlider.setBounds(50, 40, 105, 20); //音量 滑块

            running.setBounds(10, 70, 85, 20); //LiveSongs目录：
            jtfRunning.setBounds(100, 70, 250, 20); //运行目录 输入框

            allowRep.setBounds(10, 100, 105, 20); //允许重复点一首歌 戳戳框
            hMusic.setBounds(230, 100, 95, 20); //手动点歌 按钮
            hm.setBounds(125, 100, 95, 20); //填写歌曲名 输入框

            CbreakIdle.setBounds(10, 130, 105, 20); //打断空闲歌单 戳戳框
            addIdlest.setBounds(125, 130, 95, 20); //填写歌曲名 输入框
            addIdles.setBounds(230, 130, 125, 20); //添加空闲歌曲 按钮

            remove.setBounds(150, 160, 95, 20); //移除空闲歌曲 输入框
            removeButton.setBounds(260, 160, 125, 20);

            randomIdle.setBounds(10, 160, 125, 20); //随机播放空闲歌单 戳戳框

            pause.setBounds(200, 190, 175, 20); //暂停 按钮
            playIdles.setBounds(10, 190, 140, 20); //播放空闲歌单 戳戳框

            stops.setBounds(200, 220, 175, 20); //手动切歌 按钮
            button.setBounds(10, 220, 180, 20); //开始监听 按钮

            loadConfig.setBounds(10, 250, 180, 20); //加载配置 按钮
            saveConfig.setBounds(200, 250, 175, 20); //保存配置 按钮

            dm1.setBounds(180, 7, 180, 20); //弹幕1
            dm2.setBounds(180, 27, 180, 20); //弹幕2
            dm3.setBounds(180, 47, 180, 20); //弹幕3


            removeButton.addActionListener(actionEvent -> LiveSongs.removeIdles(remove.getText()));
            pause.addActionListener(e ->{
                if (!p) {
                    pause.setText("继续");
                    infoly("[音频] 已暂停");
                    p = true;

                } else {
                    pause.setText("暂停");
                    infoly("[音频] 已继续");
                    p = false;
                    synchronized (pauses){
                        pauses.notifyAll();
                    }
                }
            });
            loadConfig.addActionListener(e ->{
                try {
                    if (new File(CONFIG_PATH + "\\cfg.json").exists()) {

                        String content = new String(Files.readAllBytes(Paths.get(CONFIG_PATH + "\\cfg.json")));
                        getSettings(gson, content);
                    } else {
                        warnly("[点歌] 配置文件不存在");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            saveConfig.addActionListener(e ->{

                create();

                if (!new File(CONFIG_PATH + "\\cfg.json").exists()) {
                    try {
                        if(!new File(CONFIG_PATH + "\\cfg.json").createNewFile()){
                            errorly("[点歌] 配置创建失败！");
                            return;
                        }else {
                            infoly("[点歌] 配置创建成功");
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                JsonObject conf = new JsonObject();
                conf.addProperty("roomId", jtf.getText());
                conf.addProperty("workDir", jtfRunning.getText());
                conf.addProperty("allowReplay", LiveSongs.allowReplay);
                conf.addProperty("breakIdle", LiveSongs.breakIdles);
                conf.addProperty("volume", LiveSongs.volume);
                conf.addProperty("randomIdle", LiveSongs.randomIdles);
                conf.addProperty("format", GUI.getFormat());
                conf.addProperty("bufferSize", LiveSongs.bufferSize);
                conf.addProperty("saveMusic", saveMusics);
                conf.addProperty("onlyAdmin", permission);
                conf.addProperty("maxSongs", maxWaitToPlay);
                conf.addProperty("songCommand", songCommand);
                conf.addProperty("colddownTime", configColddownTime);
                conf.addProperty("downloadInIdle", downloadInIdle);
                conf.addProperty("alwaysOnTop", alwaysOnTop);
                conf.addProperty("sendGiftToGetChance", sendGiftToGetChance);
                JsonArray pbListJsonArray = new JsonArray();
                for (String pb : pbList) {
                    pbListJsonArray.add(pb);
                }
                conf.add("filter", pbListJsonArray);

                JsonArray blackMusicArray = new JsonArray();
                for(String blackMusics : blackMusic){
                    blackMusicArray.add(blackMusics);
                }
                conf.add("blackMusic", blackMusicArray);

                JsonArray blackPlayerArray = new JsonArray();
                for(String blackPlayers : blackPlayer){
                    blackPlayerArray.add(blackPlayers);
                }
                conf.add("blackPlayer", blackPlayerArray);
                //写完了 我先去上个厕所

                //好
                String jsonString = gson.toJson(conf);

                // 将Json字符串写入到文件中
                String filePath = CONFIG_PATH + "\\cfg.json";
                try (FileWriter fileWriter = new FileWriter(filePath)) {
                    fileWriter.write(jsonString);
                    infoly("[点歌] 配置文件保存在 " + filePath);
                } catch (IOException ex) {
                    errorly("[点歌] 配置文件保存失败");
                    ex.printStackTrace();
                }
            });
            addIdles.addActionListener(e -> new Thread(() -> {
                String video = Utils.searchSongs(addIdlest.getText());
                Utils.downloadSongs(video, "闲置", false);
                JOptionPane.showMessageDialog(null, "下载完成！", "添加空闲歌曲", JOptionPane.INFORMATION_MESSAGE);
            }).start());
            hMusic.addActionListener(e -> new Thread(()->{
                String video = Utils.searchSongs(hm.getText());
                Utils.downloadSongs(video, "主播", true);
                JOptionPane.showMessageDialog(null, "下载完成！", "手动点歌", JOptionPane.INFORMATION_MESSAGE);
            }).start());

            stops.addActionListener(e -> stop = true);


            panel.add(pause);
            panel.add(removeButton);
            panel.add(saveConfig);
            panel.add(loadConfig);
            panel.add(button);
            panel.add(stops);
            panel.add(allowRep);
            panel.add(CbreakIdle);
            panel.add(addIdles);
            panel.add(addIdlest);
            panel.add(hMusic);

            panel.add(hm);
        } // panel主要的
        {

            JScrollPane sp = new JScrollPane (LiveSongs.console); // 创建JScrollPane并将LiveSongs.console放入其中
            sp.setHorizontalScrollBarPolicy ( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // 设置水平滚动条的策略，根据需要显示或隐藏
            sp.setVerticalScrollBarPolicy ( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // 设置垂直滚动条的策略，根据需要显示或隐藏

            console.setEditable(false);


            panel2.setLayout(null);

            JLabel label = new JLabel("快进量");
            JTextField jtf = new JTextField("10");

            bufferSetter.setBounds(10,70, 100, 20);
            saveMusic.setBounds(130, 70, 150, 20);



            panel2.add(saveMusic);

            panel2.add(bufferSetter);

            panel2.add(sp);

            sp.setBounds(10, 100, 320, 160);

            LiveSongs.console.setLineWrap (true); // 设置自动换行
            LiveSongs.console.setWrapStyleWord (true); // 设置断字不断行


            label.setBounds(10, 10, 80, 20);

            jtf.setBounds(80, 10, 50, 20);

            panel2.add(label);
            panel2.add(jtf);

            saveMusic.setSelected(true);
            JButton fastReward = new JButton("快进");
            fastReward.setBounds(180, 10, 80, 20);



            saveMusic.addActionListener(e -> saveMusics = saveMusic.isSelected());
            fastReward.addActionListener(不想活了_好不容易接了个500的稿子_画完还让别人白嫖了_我真是个 -> {
                faster = Double.parseDouble(jtf.getText());
                fast = true;
            });

            panel2.add(fastReward);

            format.setBounds(10, 40, 250, 20);
            panel2.add(format);

            format.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent 我真服了这nm破代码了_出了啥问题找半年找不到_最后还得是这个B类全翻一遍_我真tm服了真是) {
                    GUI.setFormat(format.getText());
                }

                @Override
                public void keyPressed(KeyEvent 我想死啊啊啊啊啊) {
                    GUI.setFormat(format.getText());
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    GUI.setFormat(format.getText());
                }
            });
        } //次要
        {
            permissionSettings.setBounds(10, 10, 170, 20);

            permissionSettings.addActionListener(actionEvent -> {
                if(permissionSettings.isSelected()){
                    permission = 1;
                }else{
                    permission = 2;
                }
            });

            songs.add(permissionSettings);

            giftToSong.setBounds(190, 10, 170, 20);

            giftToSong.addActionListener(actionEvent -> sendGiftToGetChance = giftToSong.isSelected());

            songs.add(giftToSong);


            JLabel MaxSongsSaying = new JLabel("<-- 同一时间内等待播放列表的最大大小");
            MaxSongs.setBounds(10, 40, 55, 20);
            MaxSongsSaying.setBounds(75, 40, 220, 20);

            MaxSongs.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    try {
                        maxWaitToPlay = Integer.parseInt(MaxSongs.getText());
                    }catch (Exception ignore){}
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    try{
                        maxWaitToPlay = Integer.parseInt(MaxSongs.getText());
                    }catch (Exception ignore){}
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    try{
                        maxWaitToPlay = Integer.parseInt(MaxSongs.getText());
                    }catch (Exception ignore){}
                }
            });


            songCommandSettings.setBounds(10, 70, 120, 20);
            JLabel labelOfsongCommand = new JLabel("点歌指令");
            labelOfsongCommand.setBounds(140, 70, 100, 20);

            songCommandSettings.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    songCommand = songCommandSettings.getText();
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    songCommand = songCommandSettings.getText();
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    songCommand = songCommandSettings.getText();
                }
            });

            coldTime.setBounds(10, 100, 60, 20);
            labelOfColddown.setBounds(80, 100, 150, 20);

            coldTime.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    try {
                        configColddownTime = Integer.parseInt(coldTime.getText());
                    }catch (Exception ignore){}
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    try{
                        configColddownTime = Integer.parseInt(coldTime.getText());
                    }catch (Exception ignore){}
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    try{
                        configColddownTime = Integer.parseInt(coldTime.getText());
                    }catch (Exception ignore){}
                }
            });

            downloadInIdleSettings.setBounds(10, 130, 230, 20);

            downloadInIdleSettings.setSelected(false);
            downloadInIdleSettings.addActionListener(actionEvent -> downloadInIdle = downloadInIdleSettings.isSelected());

            playerBlack.setEditable(false);
            musicBlack.setEditable(false);

            addPlayerBlack.addActionListener(actionEvent -> {
                blackPlayer.add(playerBlackAdd.getText());
                playerBlack.addItem(playerBlackAdd.getText());
            });
            addMusicBlack.addActionListener(actionEvent -> {
                blackMusic.add(musicBlackAdd.getText());
                musicBlack.addItem(musicBlackAdd.getText());
            });

            musicBlack.setBounds(10, 160, 160, 20);
            playerBlack.setBounds(190, 160, 160, 20);

            addMusicBlack.setBounds(10, 190, 70, 20);
            addPlayerBlack.setBounds(190, 190, 70, 20);

            musicBlackAdd.setBounds(90, 190, 80, 20);
            playerBlackAdd.setBounds(270, 190, 80, 20);

            removeBlackMusic.setBounds(10, 220, 70, 20);
            removeBlackPlayer.setBounds(190, 220, 70, 20);

            removeBlackMusic.addActionListener(actionEvent -> {
                blackMusic.remove(musicBlackAdd.getText());
                musicBlack.removeItem(musicBlackAdd.getText());
            });
            
            removeBlackPlayer.addActionListener(actionEvent -> {
                blackPlayer.remove(playerBlackAdd.getText());
                playerBlack.removeItem(playerBlackAdd.getText());
            });

            for (JButton jButton : Arrays.asList(removeBlackPlayer, removeBlackMusic, addMusicBlack, addPlayerBlack)) {
                songs.add(jButton);
            }
            for (JComboBox<String> stringJComboBox : Arrays.asList(playerBlack, musicBlack)) {
                songs.add(stringJComboBox);
            }
            for (JTextField jTextField : Arrays.asList(playerBlackAdd, musicBlackAdd)) {
                songs.add(jTextField);
            }
            songs.add(downloadInIdleSettings);
            songs.add(coldTime);
            songs.add(labelOfColddown);
            songs.add(songCommandSettings);
            songs.add(labelOfsongCommand);
            songs.add(MaxSongs);
            songs.add((MaxSongsSaying));
        } //点歌
        {
            JCheckBox alwaysOnTops = new JCheckBox("始终置顶展示GUI");
            alwaysOnTops.addActionListener(actionEvent -> alwaysOnTop = alwaysOnTops.isSelected());
            alwaysOnTops.setBounds(10, 10 ,200, 20);
            gui.add(alwaysOnTops);
        } //界面

        try {
            if (new File(CONFIG_PATH + "\\cfg.json").exists()) {

                String content = new String(Files.readAllBytes(Paths.get(CONFIG_PATH + "\\cfg.json")));
                infoly("配置文件：" + content);
                    getSettings(gson, content);
            } else {
                warnly("[点歌] 配置文件不存在");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        frame.setSize(400, 345);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }

    private void getSettings(Gson gson, String content){
        JsonObject settings = gson.fromJson(content, JsonObject.class).getAsJsonObject();

        String room_id = settings.get("roomId").getAsString();
        String work_dir = settings.get("workDir").getAsString();
        String song_command = settings.get("songCommand").getAsString();
        boolean allow_replay = settings.get("allowReplay").getAsBoolean();
        boolean break_idles = settings.get("breakIdle").getAsBoolean();
        float v = settings.get("volume").getAsFloat();
        boolean random_idles = settings.get("randomIdle").getAsBoolean();
        int buffer_size = settings.get("bufferSize").getAsInt();
        boolean save_music = settings.get("saveMusic").getAsBoolean();
        int only_admin = settings.get("onlyAdmin").getAsInt();
        int max_songs = settings.get("maxSongs").getAsInt();
        int colddown_time = settings.get("colddownTime").getAsInt();
        boolean download_in_idle = settings.get("downloadInIdle").getAsBoolean();
        boolean always_on_top = settings.get("alwaysOnTop").getAsBoolean();
        boolean gift_to_get_song_chance = settings.get("sendGiftToGetChance").getAsBoolean();

        LiveSongs.roomID = room_id;
        LiveSongs.bufferSize = buffer_size;
        LiveSongs.WORK_DIR = work_dir;
        LiveSongs.volume = v;
        LiveSongs.allowReplay = allow_replay;
        LiveSongs.breakIdles = break_idles;
        LiveSongs.randomIdles = random_idles;
        LiveSongs.permission = (short) only_admin;
        LiveSongs.maxWaitToPlay = max_songs;
        LiveSongs.songCommand = song_command;
        LiveSongs.configColddownTime = colddown_time;
        LiveSongs.downloadInIdle = download_in_idle;
        LiveSongs.alwaysOnTop = always_on_top;
        LiveSongs.sendGiftToGetChance = gift_to_get_song_chance;


        saveMusics = save_music;
        GUI.setFormat(settings.get("format").getAsString());

        format.setText(settings.get("format").getAsString());


        JsonArray pbListJsonArray = settings.getAsJsonArray("filter");
        ArrayList<String> pbList = new ArrayList<>();
        for (JsonElement pbElement : pbListJsonArray) {
            String pb = pbElement.getAsString();
            pbList.add(pb);
        }
        LiveSongs.pbList = pbList;
        fs.flush();

        JsonArray musicsBlack = settings.getAsJsonArray("blackMusic");
        JsonArray playersBlack = settings.getAsJsonArray("blackPlayer");

        ArrayList<String> musicsBlackList = new ArrayList<>();
        ArrayList<String> playersBlackList = new ArrayList<>();

        for(JsonElement element : musicsBlack){
            musicsBlackList.add(element.getAsString());
        }
        for(JsonElement element : playersBlack){
            playersBlackList.add(element.getAsString());
        }
        blackPlayer = playersBlackList;
        blackMusic = musicsBlackList;

        refresh();

        infoly("[点歌] 配置文件读取成功");

        if(configColddownTime > 0){
            new Thread(()->{
                while(true){
                    if(colddownTime>0){
                        LiveSongs.flushColdTime();
                    }
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException e){}
                }
            }).start();
        }
    }
}
