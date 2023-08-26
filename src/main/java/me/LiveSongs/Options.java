package me.LiveSongs;

import com.google.gson.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicReference;

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
    final JCheckBox randomIdle = new JCheckBox("打乱空闲歌单");
    final JButton stops = new JButton("手动执行切歌");
    final JButton button = new JButton("开启监听弹幕");
    final JTextField dm1 = new JTextField("无弹幕");
    final JTextField dm2 = new JTextField("无弹幕");
    final JTextField dm3 = new JTextField("无弹幕");
    final JSlider volumesJSlider = new JSlider(-50, 0);

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
    JTextField format = new JTextField(LiveSongs.GUI.getFormat());
    JTextField bufferSetter = new JTextField("缓冲区大小");
    JTextField MaxSongs = new JTextField("16384");
    JTextField songCommandSettings = new JTextField("点歌 ");
    JLabel labelOfColddown = new JLabel("点歌冷却时间(秒)");
    JTextField coldTime = new JTextField("0");
    JCheckBox downloadInIdleSettings = new JCheckBox("用户点的歌下载到空闲歌单里");
    JComboBox<String> musicBlack = new JComboBox<>();
    JComboBox<String> playerBlack = new JComboBox<>();
    JButton addPlayerBlack = new JButton("添加");
    JButton addMusicBlack = new JButton("添加");
    JTextField playerBlackAdd = new JTextField("黑名单点歌人");
    JTextField musicBlackAdd = new JTextField("黑名单BV号");
    JButton removeBlackPlayer = new JButton("移除");
    JButton removeBlackMusic = new JButton("移除");
    JCheckBox giftToSong = new JCheckBox("送礼物获取点歌机会");
    JCheckBox useWebSocket = new JCheckBox("使用WebSocket弹幕获取");
    JTextField switchSongs = new JTextField("a");
    JLabel labelOfSwitchSongs = new JLabel("切歌快捷键");
    JButton buttonOfSwitchSongs = new JButton("更改快捷键");
    JTextField pauses = new JTextField("b");
    JButton buttonOfPause = new JButton("更改快捷键");
    JLabel labelOfPause = new JLabel("暂停快捷键");
    JTextField volumeAddTen = new JTextField("c");
    JLabel labelOfVolumeAddTen = new JLabel("音量加");
    JButton buttonOfVolumeAddTen = new JButton("更改快捷键");
    JTextField volumeSubTen = new JTextField("d");
    JLabel labelOfVolumeSubTen = new JLabel("音量减");
    JButton buttonOfVolumeSubTen = new JButton("更改快捷键");
    JCheckBox allowUserSwitchSongs = new JCheckBox("允许点歌人切歌");
    
    public void refresh() {
        for(String function : LiveSongs.hotkeyList.values()){
            switch (function){
                case "切歌":
                    char funcChar = (char) Utils.getKeyFromValue(LiveSongs.hotkeyList, "切歌");
                    switchSongs.setText(String.valueOf(funcChar));
                    break;
                case "暂停":
                    char funcChar2 = (char) Utils.getKeyFromValue(LiveSongs.hotkeyList, "暂停");
                    pauses.setText(String.valueOf(funcChar2));
                    break;
                default:
            }
        }


        jtf.setText(LiveSongs.roomID);
        volumeT.setText(String.valueOf(LiveSongs.volume));
        volumesJSlider.setValue((int) LiveSongs.volume);
        jtfRunning.setText(LiveSongs.WORK_DIR);
        allowRep.setSelected(LiveSongs.allowReplay);
        CbreakIdle.setSelected(LiveSongs.breakIdles);
        randomIdle.setSelected(LiveSongs.randomIdles);
        bufferSetter.setText(String.valueOf(LiveSongs.bufferSize));
        saveMusic.setSelected(LiveSongs.saveMusics);
        permissionSettings.setSelected(LiveSongs.permission == 1);
        MaxSongs.setText(String.valueOf(LiveSongs.maxWaitToPlay));
        songCommandSettings.setText(LiveSongs.songCommand);
        coldTime.setText(String.valueOf(LiveSongs.configColddownTime));
        downloadInIdleSettings.setSelected(LiveSongs.downloadInIdle);
        useWebSocket.setSelected(LiveSongs.usingWebSocket);

        playerBlack.removeAllItems();
        for(String s : LiveSongs.blackPlayer){
            playerBlack.addItem(s);
        }

        musicBlack.removeAllItems();
        for(String s : LiveSongs.blackMusic){
            musicBlack.addItem(s);
        }


    }

    public void start() {
        JFrame frame = new JFrame("LiveMSC 0.5.1a made by Kkforkd");
        if(new Random().nextInt(100) == 44) {
            frame.setTitle("今天也要撅一撅kk吗？❤mua~");
        }
        //要什么random，直接true
        //爱来自AA
        JPanel panel = new JPanel();
        JPanel panel2 = new JPanel(); //其他
        JPanel songs = new JPanel(); //点歌
        JPanel gui = new JPanel(); //界面
        JPanel net = new JPanel(); //网络
        JPanel hotkey = new JPanel();

        JTabbedPane mainPanel = new JTabbedPane();

        panel.setName("主要");

        panel2.setName("过滤");

        mainPanel.addTab("基础", panel);
        mainPanel.addTab("其他", panel2);
        mainPanel.addTab("过滤", fs);
        mainPanel.addTab("点歌", songs);
        mainPanel.addTab("界面", gui);
        mainPanel.addTab("网络", net);
        mainPanel.addTab("快捷键", hotkey);

        panel.setLayout(null);
        songs.setLayout(null);
        gui.setLayout(null);
        net.setLayout(null);
        hotkey.setLayout(null);
        frame.setContentPane(mainPanel);

        Gson gson = new GsonBuilder()
                .create();
        {
            jtf.setText(LiveSongs.roomID);

            volumesJSlider.setValue((int) (LiveSongs.volume));

            volumesJSlider.addChangeListener(e -> {
                try {
                    LiveSongs.volume = volumesJSlider.getValue();
                } catch (NumberFormatException ignored) {
                }
            });


            panel.add(jtfLabel);
            panel.add(jtf);

            panel.add(volumeLabel);
            panel.add(volumesJSlider);


            jtfRunning.setText(LiveSongs.WORK_DIR);

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
                    thrArray.set(LiveSongs.dList);
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


            JCheckBox playIdles = new JCheckBox("播放空闲歌单");
            playIdles.setSelected(true);


            panel.add(playIdles);

            button.addActionListener(e -> {

                LiveSongs.infoly("[初始化] 程序启动于" + LiveSongs.WORK_DIR);
                LiveSongs.blocking = false;
                LiveSongs.infoly("[点歌] 正在启动监听");
                LiveSongs.roomID = jtf.getText();
                LiveSongs.WORK_DIR = jtfRunning.getText();
                LiveSongs.breakIdles = CbreakIdle.isSelected();
                LiveSongs.allowReplay = allowRep.isSelected();
                LiveSongs.randomIdles = randomIdle.isSelected();
                LiveSongs.playIdle = playIdles.isSelected();
                try {
                    LiveSongs.bufferSize = Integer.parseInt(bufferSetter.getText());
                } catch(Exception ignore){ }
                playIdles.setEnabled(false);
                try {
                    LiveSongs.volume = Float.parseFloat(volumeT.getText());
                }catch (NumberFormatException ignore){}
                loadConfig.setEnabled(false);
                randomIdle.setEnabled(false);
                CbreakIdle.setEnabled(false);
                allowRep.setEnabled(false);
                jtfRunning.setEnabled(false);
                button.setEnabled(false);
                jtf.setEnabled(false);
                bufferSetter.setEnabled(false);

                if(!new File(LiveSongs.WORK_DIR).exists()){
                    LiveSongs.warnly("[初始化] " + LiveSongs.WORK_DIR + "目录不存在！");
                    LiveSongs.create();
                }

            });


            jtfLabel.setBounds(10, 10, 65, 22); //直播间ID
            jtf.setBounds(70, 10, 65, 22); //直播间id 输入框
            jtf.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            volumeLabel.setBounds(10, 40, 45, 22); //音量：
            volumeLabel.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            volumesJSlider.setBounds(50, 40, 105, 22); //音量 滑块

            running.setBounds(10, 70, 85, 22); //LiveSongs目录：
            running.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            jtfRunning.setBounds(100, 70, 250, 22); //运行目录 输入框
            jtfRunning.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            allowRep.setBounds(10, 100, 105, 22); //允许重复点一首歌 戳戳框
            allowRep.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            hMusic.setBounds(230, 100, 95, 22); //手动点歌 按钮
            hMusic.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            hm.setBounds(125, 100, 95, 22); //填写歌曲名 输入框
            hm.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            CbreakIdle.setBounds(10, 130, 105, 22); //打断空闲歌单 戳戳框
            CbreakIdle.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            addIdlest.setBounds(125, 130, 95, 22); //填写歌曲名 输入框
            addIdlest.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            addIdles.setBounds(230, 130, 125, 22); //添加空闲歌曲 按钮
            addIdles.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            remove.setBounds(125, 160, 95, 22); //移除空闲歌曲 输入框
            remove.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            removeButton.setBounds(230, 160, 125, 22);
            removeButton.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            randomIdle.setBounds(10, 160, 105, 22); //随机播放空闲歌单 戳戳框
            randomIdle.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            pause.setBounds(200, 190, 175, 22); //暂停 按钮
            pause.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            playIdles.setBounds(10, 190, 140, 22); //播放空闲歌单 戳戳框
            playIdles.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            stops.setBounds(200, 220, 175, 22); //手动切歌 按钮
            stops.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            button.setBounds(10, 220, 180, 22); //开始监听 按钮
            button.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            loadConfig.setBounds(10, 250, 180, 22); //加载配置 按钮
            loadConfig.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            saveConfig.setBounds(200, 250, 175, 22); //保存配置 按钮
            saveConfig.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            dm1.setBounds(180, 7, 180, 22); //弹幕1
            dm2.setBounds(180, 27, 180, 22); //弹幕2
            dm3.setBounds(180, 47, 180, 22); //弹幕3


            removeButton.addActionListener(actionEvent -> LiveSongs.removeIdles(remove.getText()));
            pause.addActionListener(e ->{
                if (!p) {
                    pause.setText("继续");
                    LiveSongs.infoly("[音频] 已暂停");
                    p = true;

                } else {
                    pause.setText("暂停");
                    LiveSongs.infoly("[音频] 已继续");
                    p = false;
                    synchronized (LiveSongs.pauses){
                        LiveSongs.pauses.notifyAll();
                    }
                }
            });
            loadConfig.addActionListener(e ->{
                try {
                    if (new File(LiveSongs.CONFIG_PATH + "\\cfg.json").exists()) {

                        String content = new String(Files.readAllBytes(Paths.get(LiveSongs.CONFIG_PATH + "\\cfg.json")));
                        getSettings(gson, content);
                    } else {
                        LiveSongs.warnly("[点歌] 配置文件不存在");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            saveConfig.addActionListener(e ->{

                LiveSongs.create();

                if (!new File(LiveSongs.CONFIG_PATH + "\\cfg.json").exists()) {
                    try {
                        if(!new File(LiveSongs.CONFIG_PATH + "\\cfg.json").createNewFile()){
                            LiveSongs.errorly("[点歌] 配置创建失败！");
                            return;
                        }else {
                            LiveSongs.infoly("[点歌] 配置创建成功");
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
                conf.addProperty("format", LiveSongs.GUI.getFormat());
                conf.addProperty("bufferSize", LiveSongs.bufferSize);
                conf.addProperty("saveMusic", LiveSongs.saveMusics);
                conf.addProperty("onlyAdmin", LiveSongs.permission);
                conf.addProperty("maxSongs", LiveSongs.maxWaitToPlay);
                conf.addProperty("songCommand", LiveSongs.songCommand);
                conf.addProperty("colddownTime", LiveSongs.configColddownTime);
                conf.addProperty("downloadInIdle", LiveSongs.downloadInIdle);
                conf.addProperty("alwaysOnTop", LiveSongs.alwaysOnTop);
                conf.addProperty("sendGiftToGetChance", LiveSongs.sendGiftToGetChance);
                conf.addProperty("usingWebSocket", LiveSongs.usingWebSocket);

                JsonArray pbListJsonArray = new JsonArray();
                for (String pb : LiveSongs.pbList) {
                    pbListJsonArray.add(pb);
                }
                conf.add("filter", pbListJsonArray);

                JsonArray blackMusicArray = new JsonArray();
                for(String blackMusics : LiveSongs.blackMusic){
                    blackMusicArray.add(blackMusics);
                }
                conf.add("blackMusic", blackMusicArray);

                JsonArray blackPlayerArray = new JsonArray();
                for(String blackPlayers : LiveSongs.blackPlayer){
                    blackPlayerArray.add(blackPlayers);
                }
                conf.add("blackPlayer", blackPlayerArray);

                JsonObject hotkeyList = new JsonObject();
                for(char keys : LiveSongs.hotkeyList.keySet()){
                    hotkeyList.addProperty(LiveSongs.hotkeyList.get(keys), keys);
                }
                conf.add("hotkeyList", hotkeyList);









                String jsonString = gson.toJson(conf);

                String filePath = LiveSongs.CONFIG_PATH + "\\cfg.json";
                try (FileWriter fileWriter = new FileWriter(filePath)) {
                    fileWriter.write(jsonString);
                    LiveSongs.infoly("[点歌] 配置文件保存在 " + filePath);
                } catch (IOException ex) {
                    LiveSongs.errorly("[点歌] 配置文件保存失败");
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

            stops.addActionListener(e -> LiveSongs.stop = true);


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

            LiveSongs.console.setEditable(false);


            panel2.setLayout(null);

            JLabel label = new JLabel("快进量");
            JTextField jtf = new JTextField("10");

            bufferSetter.setBounds(10,70, 100, 22);
            saveMusic.setBounds(130, 70, 150, 22);



            panel2.add(saveMusic);

            panel2.add(bufferSetter);

            panel2.add(sp);

            sp.setBounds(10, 100, 320, 160);

            LiveSongs.console.setLineWrap (true); // 设置自动换行
            LiveSongs.console.setWrapStyleWord (true); // 设置断字不断行


            label.setBounds(10, 10, 80, 22);

            jtf.setBounds(80, 10, 50, 22);

            panel2.add(label);
            panel2.add(jtf);

            saveMusic.setSelected(true);
            JButton fastReward = new JButton("快进");
            fastReward.setBounds(180, 10, 80, 22);



            saveMusic.addActionListener(e -> LiveSongs.saveMusics = saveMusic.isSelected());
            fastReward.addActionListener(不想活了_好不容易接了个500的稿子_画完还让别人白嫖了_我真是个 -> {
                LiveSongs.faster = Double.parseDouble(jtf.getText());
                LiveSongs.fast = true;
            });

            panel2.add(fastReward);

            format.setBounds(10, 40, 250, 22);
            panel2.add(format);

            format.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent 我真服了这nm破代码了_出了啥问题找半年找不到_最后还得是这个B类全翻一遍_我真tm服了真是) {
                    LiveSongs.GUI.setFormat(format.getText());
                }

                @Override
                public void keyPressed(KeyEvent 我想死啊啊啊啊啊) {
                    LiveSongs.GUI.setFormat(format.getText());
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    LiveSongs.GUI.setFormat(format.getText());
                }
            });
        }
        {
            permissionSettings.setBounds(10, 10, 105, 22);

            permissionSettings.addActionListener(actionEvent -> {
                if(permissionSettings.isSelected()){
                    LiveSongs.permission = 1;
                }else{
                    LiveSongs.permission = 2;
                }
            });

            songs.add(permissionSettings);

            giftToSong.setBounds(120, 10, 140, 22);

            giftToSong.addActionListener(actionEvent -> LiveSongs.sendGiftToGetChance = giftToSong.isSelected());

            allowUserSwitchSongs.addActionListener(actionEvent -> LiveSongs.allowUsersSwitchSongs = allowUserSwitchSongs.isSelected());
            allowUserSwitchSongs.setBounds(260, 10, 150, 22);

            songs.add(allowUserSwitchSongs);

            songs.add(giftToSong);


            JLabel MaxSongsSaying = new JLabel("<-- 同一时间内等待播放列表的最大大小");
            MaxSongs.setBounds(10, 40, 55, 22);
            MaxSongsSaying.setBounds(75, 40, 220, 22);

            MaxSongs.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    try {
                        LiveSongs.maxWaitToPlay = Integer.parseInt(MaxSongs.getText());
                    }catch (Exception ignore){}
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    try{
                        LiveSongs.maxWaitToPlay = Integer.parseInt(MaxSongs.getText());
                    }catch (Exception ignore){}
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    try{
                        LiveSongs.maxWaitToPlay = Integer.parseInt(MaxSongs.getText());
                    }catch (Exception ignore){}
                }
            });


            songCommandSettings.setBounds(10, 70, 120, 22);
            JLabel labelOfsongCommand = new JLabel("点歌指令");
            labelOfsongCommand.setBounds(140, 70, 100, 22);

            songCommandSettings.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    LiveSongs.songCommand = songCommandSettings.getText();
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    LiveSongs.songCommand = songCommandSettings.getText();
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    LiveSongs.songCommand = songCommandSettings.getText();
                }
            });

            coldTime.setBounds(10, 100, 60, 22);
            labelOfColddown.setBounds(80, 100, 150, 22);

            coldTime.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    try {
                        LiveSongs.configColddownTime = Integer.parseInt(coldTime.getText());
                    }catch (Exception ignore){}
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    try{
                        LiveSongs.configColddownTime = Integer.parseInt(coldTime.getText());
                    }catch (Exception ignore){}
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    try{
                        LiveSongs.configColddownTime = Integer.parseInt(coldTime.getText());
                    }catch (Exception ignore){}
                }
            });

            downloadInIdleSettings.setBounds(10, 130, 230, 22);

            downloadInIdleSettings.setSelected(false);
            downloadInIdleSettings.addActionListener(actionEvent -> LiveSongs.downloadInIdle = downloadInIdleSettings.isSelected());

            playerBlack.setEditable(false);
            musicBlack.setEditable(false);

            addPlayerBlack.addActionListener(actionEvent -> {
                LiveSongs.blackPlayer.add(playerBlackAdd.getText());
                playerBlack.addItem(playerBlackAdd.getText());
            });
            addMusicBlack.addActionListener(actionEvent -> {
                LiveSongs.blackMusic.add(musicBlackAdd.getText());
                musicBlack.addItem(musicBlackAdd.getText());
            });

            musicBlack.setBounds(10, 160, 160, 22);
            playerBlack.setBounds(190, 160, 160, 22);

            addMusicBlack.setBounds(10, 190, 75, 22);
            addPlayerBlack.setBounds(190, 190, 75, 22);

            musicBlackAdd.setBounds(90, 190, 80, 22);
            playerBlackAdd.setBounds(270, 190, 80, 22);

            removeBlackMusic.setBounds(10, 220, 75, 22);
            removeBlackPlayer.setBounds(190, 220, 75, 22);



            removeBlackMusic.addActionListener(actionEvent -> {
                LiveSongs.blackMusic.remove(musicBlackAdd.getText());
                musicBlack.removeItem(musicBlackAdd.getText());
                musicBlackAdd.setText("黑名单BV号");
            });
            
            removeBlackPlayer.addActionListener(actionEvent -> {
                LiveSongs.blackPlayer.remove(playerBlackAdd.getText());
                playerBlack.removeItem(playerBlackAdd.getText());
                playerBlackAdd.setText("黑名单点歌人");
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
            alwaysOnTops.addActionListener(actionEvent -> LiveSongs.alwaysOnTop = alwaysOnTops.isSelected());
            alwaysOnTops.setBounds(10, 10 ,200, 22);
            gui.add(alwaysOnTops);
        } //界面
        {
            useWebSocket.setSelected(true);
            useWebSocket.setBounds(10, 10, 200, 22);
            net.add(useWebSocket);
            useWebSocket.addActionListener(actionEvent -> LiveSongs.usingWebSocket = useWebSocket.isSelected());
        } //网络
        {
            switchSongs.setBounds(10, 10, 20, 22);
            switchSongs.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            labelOfSwitchSongs.setBounds(40, 10, 60, 22);
            labelOfSwitchSongs.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            pauses.setBounds(10, 30, 20, 22);
            pauses.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));
            labelOfPause.setBounds(40, 30, 60, 22);
            labelOfPause.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            buttonOfSwitchSongs.setBounds(140, 10, 100, 22);
            buttonOfSwitchSongs.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            buttonOfPause.setBounds(140, 30, 100, 22);
            buttonOfPause.setFont(new Font("Microsoft YaHei",Font.PLAIN,12));

            buttonOfSwitchSongs.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    LiveSongs.hotkeyList.remove(Utils.getKeyFromValue(LiveSongs.hotkeyList, "切歌"), "切歌");
                    LiveSongs.hotkeyList.put(switchSongs.getText().charAt(0), "切歌");
                }
            });

            buttonOfPause.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    LiveSongs.hotkeyList.remove(Utils.getKeyFromValue(LiveSongs.hotkeyList, "暂停"), "暂停");
                    LiveSongs.hotkeyList.put(pauses.getText().charAt(0), "暂停");
                }
            });

            hotkey.add(buttonOfPause);
            hotkey.add(buttonOfSwitchSongs);
            hotkey.add(switchSongs);
            hotkey.add(labelOfSwitchSongs);
            hotkey.add(pauses);
            hotkey.add(labelOfPause);
        }

        try {
            if (new File(LiveSongs.CONFIG_PATH + "\\cfg.json").exists()) {

                String content = new String(Files.readAllBytes(Paths.get(LiveSongs.CONFIG_PATH + "\\cfg.json")));
                LiveSongs.infoly("配置文件：" + content);
                    getSettings(gson, content);
            } else {
                LiveSongs.warnly("[点歌] 配置文件不存在");
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
        boolean use_websocket = settings.get("usingWebSocket").getAsBoolean();

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
        LiveSongs.usingWebSocket = use_websocket;


        LiveSongs.saveMusics = save_music;
        LiveSongs.GUI.setFormat(settings.get("format").getAsString());

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
        LiveSongs.blackPlayer = playersBlackList;
        LiveSongs.blackMusic = musicsBlackList;

        LiveSongs.hotkeyList.clear();
        JsonObject keyList = settings.get("hotkeyList").getAsJsonObject();
        for(String keys : keyList.keySet()){
            LiveSongs.hotkeyList.put(keyList.get(keys).getAsCharacter(), keys);
        }

        refresh();

        LiveSongs.infoly("[点歌] 配置文件读取成功");
    }
}
