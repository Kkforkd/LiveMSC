package me.LiveSongs;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;
import java.util.Timer;

class Audios implements Runnable{
    public final AudioInputStream audioInputStream;
    public final SourceDataLine line;
    private final File audioFile;
    int sizes = 0;
    boolean flag = true;
    private final Timer timer = new Timer("VolumeSetting");
    public Audios(AudioInputStream audioInputStream, SourceDataLine line, File audioFile){
        this.audioInputStream = audioInputStream;
        this.line = line;
        this.audioFile = audioFile;
    }

    @Override
    public void run() {
        try {

            byte[] buffer = new byte[LiveSongs.bufferSize];
            int bytesRead;


            FloatControl fc = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);


            while ((bytesRead = audioInputStream.read(buffer)) != -1 && flag) {
                if(Options.p){
                    synchronized (LiveSongs.pauses) {
                        LiveSongs.pauses.wait();
                    }
                }

                line.write(buffer, 0, bytesRead);
                sizes += bytesRead;

                fc.setValue(LiveSongs.volume);
            }
            flag = false;



        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        line.drain();
        line.stop();
        line.close();
        timer.cancel();
    }

    public boolean isAlive(){
        return this.flag;
    }

    public void s() {
        flag = false;
    }

    public void fastForward(double percentage) {
        if (percentage <= 0 || percentage >= 100) {
            LiveSongs.logger.error(("Percentage must be between 0 and 100."));
            return;
        }

        long fileSize = audioFile.length();
        double targetSize = ((percentage / 100) * fileSize);

        try {
            audioInputStream.skip((long) targetSize);
            sizes += targetSize;
        } catch (IOException ignored) {
        }
    }
}