package helei.domain;

import helei.game.GameFrame;
import helei.util.Constant;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Me extends People {
    //自身人物的配置文件
    private static Properties prop;
    //人物图片
    public static Map<String,Image[] > meImgs;

    //
    static {
        meImgs = new HashMap<>();
        prop=new Properties();
        try {
            prop.load(GameFrame.class.getClassLoader().getResourceAsStream("properties/me.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int type = Integer.parseInt(prop.getProperty("me_type_num"));
        for(int i=0 ;i<type;i++){
            String key = "myImg"+i;
            //初始化动作图片
            Image[] value = new Image[10];

            value[0]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_move_r"));
            value[1]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_move_l"));
            value[2]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_move_l"));
            value[3]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_move_r"));
            value[4]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_stand_l"));
            value[5]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_stand_r"));
            value[6]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_attack_l"));
            value[7]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_attack_r"));
            value[8]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_die"));
            value[9]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("me"+i+"_victory"));

            meImgs.put(key,value);
        }
    }

    //血条
    private BloodBar bar = new BloodBar();
    //当前人物的图片
    private Image[] img;
    //人物类型
    public int type ;

    public Me(int x, int y, int state ,int type) {
        super(x, y, state);
        this.type = type;
        init(this.type);
    }


    /**
     * 初始化对象参数
     * @param type
     */
    public void init(int type){
        this.hp = Integer.parseInt(prop.getProperty("me"+type+"_hp"));
        this.maxHp = this.hp;
        this.atk = Integer.parseInt(prop.getProperty("me"+type+"_atk"));
        this.speed = Integer.parseInt(prop.getProperty("me"+type+"_move_speed"));
        this.boomSpeed = Integer.parseInt(prop.getProperty("me"+type+"_boom_speed"));
        this.atkSpeed = Integer.parseInt(prop.getProperty("me"+type+"_atk_speed"));
        this.name = prop.getProperty("me"+type+"_name");
        img = meImgs.get("myImg"+type);
    }


    /**
     * 自身角色的移动，人物的坐标x，y为相对屏幕的坐标。
     * 移动时，若屏幕边缘没有触碰到地图边缘，则移动地图，人物不动，
     * 若触碰到地图边缘，则移动人物
     */
    protected void move(){
        switch (state){
            case Constant.DIR_UP:
                if(y> Constant.FRAME_HEIGHT/2){
                    y -=speed;
                }else {
                    GameFrame.mapY -= speed;
                    if (GameFrame.mapY < 0) {
                        GameFrame.mapY = 0;
                    }
                    if (GameFrame.mapY == 0)
                        y -= speed;
                    if (y < RADIUS + GameFrame.titleHeight) y = RADIUS + GameFrame.titleHeight;
                }
                break;
            case Constant.DIR_DOWN:
                if(y < Constant.FRAME_HEIGHT/2){
                    y+=speed;
                    if (y > Constant.FRAME_HEIGHT/2) y = Constant.FRAME_HEIGHT/2 ;
                }else {
                    GameFrame.mapY += speed;
                    if (GameFrame.mapY > GameFrame.mapHeight - Constant.FRAME_HEIGHT) {
                        GameFrame.mapY = GameFrame.mapHeight - Constant.FRAME_HEIGHT;
                    }
                    if(GameFrame.mapY == GameFrame.mapHeight - Constant.FRAME_HEIGHT)
                        y+=speed;
                    if(y > Constant.FRAME_HEIGHT - RADIUS) y =Constant.FRAME_HEIGHT - RADIUS;
                }
                break;
            case Constant.DIR_LEFT:
                if(x> Constant.FRAME_WIDTH/2){
                    x -=speed;
                }else {
                    GameFrame.mapX -= speed;
                    if (GameFrame.mapX < 0) {
                        GameFrame.mapX = 0;
                    }
                    if (GameFrame.mapX == 0)
                        x -= speed;
                    if (x < RADIUS) x = RADIUS;
                }
                break;
            case Constant.DIR_RIGHT:
                if(x < Constant.FRAME_WIDTH/2){
                    x+=speed;
                }else {
                    GameFrame.mapX += speed;
                    if (GameFrame.mapX > GameFrame.mapWidth - Constant.FRAME_WIDTH) {
                        GameFrame.mapX = GameFrame.mapWidth - Constant.FRAME_WIDTH;
                    }
                    if (GameFrame.mapX == GameFrame.mapWidth - Constant.FRAME_WIDTH)
                        x += speed;
                    if (x > Constant.FRAME_WIDTH - RADIUS) x = Constant.FRAME_WIDTH -RADIUS;
                }
                break;
        }
    }



    /**
     * 画自己
     * @param g
     */
    public void draw(Graphics g){
        if(hp<=0) state = Constant.STATE_DIE;
        if(state== Constant.DIR_UP || state==Constant.DIR_DOWN||state==Constant.DIR_LEFT||state==Constant.DIR_RIGHT) {
            move();
        }
        g.drawImage(img[state],x-RADIUS,y-RADIUS,null);
        //画子弹
        drawBoom(g);
        //画名字
        drawName(g);
        //画血条
        bar.drawMe(g);
    }

}
