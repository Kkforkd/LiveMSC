package me.LiveSongs;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayers {
    public final File audioFile;
    private final float volume;
    public Thread audioThread;
    public Audios a;
    boolean flags = true;
    public AudioPlayers(File audioFile, float volume) {
        this.audioFile = audioFile;
        this.volume = volume;
        LiveSongs.infoly("[音频] 初始化为" + volume + "音量");
        LiveSongs.infoly("[音频] 播放" + audioFile.getName());
    }

    public void play() throws IOException {

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

            AudioFormat format = audioInputStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            FloatControl c = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            c.setValue(volume);

            line.start();

            a = new Audios(audioInputStream, line, audioFile);
            audioThread = new Thread(a, "PlayThread");
            audioThread.start();
            flags = false;
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isAlive(){
        return a.isAlive();
    }



}
