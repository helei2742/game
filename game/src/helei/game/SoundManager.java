package helei.game;

import helei.util.MyUtil;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SoundManager {

    private static Properties prop;
    //运行时角色的声音数
    private static final int RUN_SOUND_NUM;
    //运行时的bgm数量
    private static final int RUN_BGM_NUM;

    //角色声音
    private int currentSound = 0;
    private static SourceDataLine roleVoice = null;
    private static AudioInputStream asRoleVoice = null;
    private static Thread roleVoiceThread = null;

    //bgm
    private static SourceDataLine bgm = null;
    private static AudioInputStream asBgm = null;
    private static Thread bgmThread = null;

    static {
        prop = new Properties();
        try {
            prop.load(SoundManager.class.getClassLoader().getResourceAsStream("properties/voice.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioInputStream as;
        try {
            as = AudioSystem.getAudioInputStream(new File(prop.getProperty("init_Sound")));
            AudioFormat format = as.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            bgm = (SourceDataLine) AudioSystem.getLine(info);
            roleVoice = (SourceDataLine) AudioSystem.getLine(info);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        RUN_SOUND_NUM = Integer.parseInt(prop.getProperty("run_sound_num"));
        RUN_BGM_NUM = Integer.parseInt(prop.getProperty("bgm_run_num"));
    }


    /**
     * 播放角色死亡时声音的方法
     * @param role
     */
    public void playRoleDieVoice(int role){
        clear(roleVoiceThread,roleVoice,asRoleVoice);
        roleVoiceThread = new Thread(()->{
            if(MyUtil.getRandomBool()) {
                play(new File(prop.getProperty("v" + role + "_die_1")));
            }else{
                play(new File(prop.getProperty("v" + role + "_die_0")));
            }
        });
        roleVoiceThread.start();
    }


    /**
     * 播放角色胜利时的方法
     * @param role
     */
    public void playRoleVictoryVoice(int role){
        clear(roleVoiceThread,roleVoice,asRoleVoice);
        roleVoiceThread = new Thread(()->{
                if(MyUtil.getRandomBool()) {
                    play(new File(prop.getProperty("v" + role + "_victory_1")));
                }else{
                    play(new File(prop.getProperty("v" + role + "_victory_0")));
                }
        });
        roleVoiceThread.start();
    }

    /**
     * 播放角色选择界面的方法
     * @param role
     */
    public void playRoleSelectVoice(int role){
        clear(roleVoiceThread,roleVoice,asRoleVoice);
        roleVoiceThread = new Thread(()->{
                if(MyUtil.getRandomBool()) {
                    play(new File(prop.getProperty("v" + role + "_select_1")));
                }else{
                    play(new File(prop.getProperty("v" + role + "_select_0")));
                }
        });
        roleVoiceThread.start();
    }

    /**
     * 播放运行时的角色声音
     * @param role
     */
    public void playRunVoice(int role){
        clear(roleVoiceThread,roleVoice,asRoleVoice);
        roleVoiceThread = new Thread(()->{
                play(new File(prop.getProperty("v" + role + "_run_"+currentSound)));
                currentSound++;
                if(currentSound == RUN_SOUND_NUM) currentSound=0;
        });
        roleVoiceThread.start();
    }

    /**
     * 开始菜单的bgm
     */
    public void menuBgm(){
        clear(bgmThread,bgm,asBgm);
        bgmThread = new Thread(()->{
                playBgm(new File(prop.getProperty("bgm_menu")),false);
        });
        bgmThread.start();
    }

    /**
     * 选择存档的bgm
     */
    public void saveBgm(){
        clear(bgmThread,bgm,asBgm);
        bgmThread = new Thread(()->{
            playBgm(new File(prop.getProperty("bgm_save")),false);
        });
        bgmThread.start();
    }

    /**
     * 回收资源方法
     * @param thread
     * @param sdl
     * @param is
     */
    private void clear(Thread thread,SourceDataLine sdl,InputStream is){
        if(sdl!=null&&sdl.isActive()){
            sdl.flush();
        }
        if(thread!=null&& thread.isAlive()){
            thread.stop();
            thread = null;
        }
        if(is!=null){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 运行时的bgm
     */
    public void runBgm(){
        clear(bgmThread,bgm,asBgm);
        bgmThread = new Thread(()->{
            playBgm(new File(prop.getProperty("bgm_run_"+MyUtil.getRandomNumber(0,RUN_BGM_NUM-1))),true);
        });
        bgmThread.start();
    }

    /**
     * 帮助界面的bgm
     */
    public void helpBgm(){
        clear(bgmThread,bgm,asBgm);
        bgmThread = new Thread(()->{
            playBgm(new File(prop.getProperty("bgm_help")),false);
        });
        bgmThread.start();
    }

    /**
     * 关于界面的bgm
     */
    public void aboutBgm(){
        clear(bgmThread,bgm,asBgm);
        bgmThread = new Thread(()->{
            playBgm(new File(prop.getProperty("bgm_about")),false);
        });
        bgmThread.start();
    }

    /**
     * 角色选择界面的bgm
     */
    public void roleBgm(){
        clear(bgmThread,bgm,asBgm);
        bgmThread = new Thread(()->{
            playBgm(new File(prop.getProperty("bgm_role")),false);
        });
        bgmThread.start();
    }

    /**
     * 地图选择界面bgm
     */
    public void mapBgm(){
        clear(bgmThread,bgm,asBgm);
        bgmThread = new Thread(()->{
            playBgm(new File(prop.getProperty("bgm_map")),false);
        });
        bgmThread.start();
    }

    /**
     *  因为BGM和人物声音都必须要达到下一个语言来时立即结束未播放的语言，这里必须写成两个方法
     *
     * @param bgmFile 音频文件的位置
     * @param isRun   判断是否是正在游戏，如果是true会随机播放运行的bgm，如果为false则会循环播放
     */
    private void playBgm(File bgmFile,boolean isRun){
        try {
            asBgm = AudioSystem.getAudioInputStream(bgmFile);
            AudioFormat format = asBgm.getFormat();
            bgm.open(format);
            bgm.start();
            int nBytesRead = 0;
            byte[] abData = new byte[1024];
            while (nBytesRead != -1) {
                nBytesRead = asBgm.read(abData, 0, abData.length);
                if (nBytesRead >= 0)
                    bgm.write(abData, 0, nBytesRead);
            }

            if(isRun){
                clear(bgmThread, bgm , asBgm);
                runBgm();
            }else {
                clear(null, bgm, asBgm);
                //释放资源
                playBgm(bgmFile,false);
            }
        }catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放角色声音的方法
     * @param voiceFile
     */
    private void play(File voiceFile){
        try {
            asRoleVoice = AudioSystem.getAudioInputStream(voiceFile);//音频文件在项目根目录的img文件夹下
            AudioFormat format = asRoleVoice.getFormat();

            roleVoice.open(format);
            roleVoice.start();
            int nBytesRead = 0;
            byte[] abData = new byte[1024];
            while (nBytesRead != -1) {
                nBytesRead = asRoleVoice.read(abData, 0, abData.length);
                if (nBytesRead >= 0)
                   roleVoice.write(abData, 0, nBytesRead);
            }

            asRoleVoice.close();
            roleVoice.drain();
            roleVoice.close();
        }catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
