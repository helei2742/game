package helei.test;


import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class Video {
    public static void main(String[] args) throws IOException, JavaLayerException {

        AudioInputStream as;
        try {
            as = AudioSystem.getAudioInputStream(new File("D:\\ideaWorkSpace\\tankgame\\game\\voice\\roleVoice\\98k\\role_attack_0.wav"));//音频文件在项目根目录的img文件夹下
            AudioFormat format = as.getFormat();
            SourceDataLine sdl = null;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(format);
            sdl.start();
            int nBytesRead = 0;
            byte[] abData = new byte[512];
            while (nBytesRead != -1) {
                nBytesRead = as.read(abData, 0, abData.length);
                if (nBytesRead >= 0)
                    sdl.write(abData, 0, nBytesRead);
            }
            //关闭SourceDataLine
            sdl.drain();
            sdl.close();
        }catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
