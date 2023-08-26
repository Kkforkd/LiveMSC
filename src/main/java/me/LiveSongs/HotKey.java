package me.LiveSongs;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.HashMap;
import java.util.Map;

public class HotKey implements NativeKeyListener {

    private final Map<Character, String> keyList;
    public HotKey(Map<Character, String> keyList){
        this.keyList = keyList;
        GlobalScreen.addNativeKeyListener(this);
    }
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        String function = keyList.get(nativeEvent.getKeyChar());
        if(function == null){
            return;
        }
        switch(function){
            case "切歌":
                LiveSongs.stop = true;
                break;
            case "暂停":
                if (!Options.p) {
                    LiveSongs.Opt.pause.setText("继续");
                    LiveSongs.infoly("[音频] 已暂停");
                    Options.p = true;

                } else {
                    LiveSongs.Opt.pause.setText("暂停");
                    LiveSongs.infoly("[音频] 已继续");
                    Options.p = false;
                    synchronized (LiveSongs.pauses){
                        LiveSongs.pauses.notifyAll();
                    }
                }
                break;
            case "音量加十":
                LiveSongs.volume += 10;
                break;
            case "音量减十":
                LiveSongs.volume -= 10;
                break;
            case "快进":
                LiveSongs.faster = 10.0;
                LiveSongs.fast = true;
                break;
            default:
                LiveSongs.errorly("没有对应的功能");
        }

    }
}
/*

 */