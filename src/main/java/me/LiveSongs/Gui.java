package me.LiveSongs;

import javax.swing.*;
import java.awt.*;
import java.util.TimerTask;

public class Gui {
    String playingMusic;
    String playerName;
    private String format = "当前正在播放：[歌曲名]    由 [点歌人] 点歌    还有 [剩余歌曲数] 首歌等待播放";

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void openGUI(){
        JFrame frame = new JFrame("kk牌点歌寄");
        JPanel panel = new JPanel();
        frame.setSize(1000, 35);
        frame.setAlwaysOnTop(LiveSongs.alwaysOnTop);

        frame.setContentPane(panel);
        frame.setSize(1000, 35);

        JLabel label = new JLabel("当前正在播放：" + playingMusic);
        frame.setSize(1000, 35);
        label.setFont(new Font("Microsoft YaHei",Font.BOLD,20));
        frame.setSize(1000, 35);
        label.setOpaque(true);
        frame.setSize(1000, 35);
        java.util.Timer flushTimer = new java.util.Timer("FlushTimer");
        frame.setSize(1000, 35);
        flushTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if(playingMusic == null || playerName == null){
                    return;
                }
                frame.setAlwaysOnTop(LiveSongs.alwaysOnTop);
                label.setText(format
                        .replace("[歌曲名]", playingMusic)
                        .replace("[点歌人]", playerName)
                        .replace("[剩余歌曲数]", String.valueOf((LiveSongs.waitToPlay.size() == 0) ? 0 : LiveSongs.waitToPlay.size() - 1) ));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
        frame.setSize(1000, 35);
        panel.add(label);
        frame.setSize(1000, 35);
        frame.setVisible(true);
    }

    public void setPlayingMusic(String playingMusic, String playerName) {
        this.playingMusic = playingMusic;
        this.playerName = playerName;
    }
}
