package helei.domain;

import helei.game.GameFrame;
import helei.util.Constant;
import helei.util.MyUtil;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Enemy extends People {
    //敌人的配置文件
    public static Properties prop;
    //敌人的图片
    private final static Map<String,Image[]> enemyImgs;
    //敌人动作改变时间
    private final static int[] ENEMY_AI_INTERVAL;

    static {
        enemyImgs = new HashMap<>();

        prop=new Properties();
        try {
            prop.load(GameFrame.class.getClassLoader().getResourceAsStream("properties/enemy.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int type = Integer.parseInt(prop.getProperty("enemy_type_num"));
        ENEMY_AI_INTERVAL = new int[type];

        for(int i = 0; i < type ; i++) {
            ENEMY_AI_INTERVAL[i] = Integer.parseInt(prop.getProperty("e"+i+"_ai_interval"));

            String key = "enemy"+i;
            Image[] value = new Image[10];
            value[0] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_move_r"));
            value[1] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_move_l"));
            value[2] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_move_l"));
            value[3] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_move_r"));
            value[4] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_stand_l"));
            value[5] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_stand_r"));
            value[6] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_attack_l"));
            value[7] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_attack_r"));
            value[8] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_die"));
            value[9] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("E"+i+"_vic"));
            enemyImgs.put(key,value);
        }
    }

    //敌人对象的图片
    private Image[] img;
    //记录开始时间
    private long aiTime;
    //敌人对象的类型
    private int type ;

    private BloodBar bar = new BloodBar();

    public Enemy(int x, int y, int state,int type) {
        super(x, y, state);
        this.type = type;
        init(type);
        aiTime = System.currentTimeMillis();
        name = MyUtil.getRandomName();
    }



    /**
     * 敌人对象创建时初始化
     */
    private void init(int type){
        this.hp = Integer.parseInt(prop.getProperty("e"+type+"_hp"));
        this.maxHp = this.hp;
        this.atk = Integer.parseInt(prop.getProperty("e"+type+"_atk"));
        this.speed = Integer.parseInt(prop.getProperty("e"+type+"_move_speed"));
        this.boomSpeed = Integer.parseInt(prop.getProperty("e"+type+"_boom_speed"));
        this.atkSpeed = Integer.parseInt(prop.getProperty("e"+type+"_atk_speed"));
        img = Enemy.enemyImgs.get("enemy"+type);
    }

    /**
     * 创建敌方
     * @return
     */
    public static Enemy createEnemy(){
        int x = MyUtil.getRandomNumber(0,GameFrame.mapWidth-RADIUS);
        int y = MyUtil.getRandomNumber(GameFrame.titleHeight,GameFrame.mapHeight-RADIUS);
        int type = MyUtil.getRandomNumber(0,Integer.parseInt(prop.getProperty("enemy_type_num")));
        Enemy enemy = new Enemy(x, y, Constant.STATE_STAND_R, type);
        enemy.setEnemy(true);
        return enemy;
    }

    /**
     * 画敌人
     * @param g
     */
    public void draw(Graphics g){
        if(hp<=0){
            state = Constant.STATE_DIE;
        }
        if(state== Constant.DIR_UP || state==Constant.DIR_DOWN||state==Constant.DIR_LEFT||state==Constant.DIR_RIGHT) {
            move();
        }
        ai();

        if(MyUtil.isInScreen(x,y)) {
            //画名字血条
            g.drawImage(img[state], MyUtil.getScreenPositionX(x) - RADIUS,
                    MyUtil.getScreenPositionY(y) - RADIUS, null);
            drawName(g);
            bar.draw(g);
        }
        //画子弹
        drawBoom(g);

    }
    /**
     * 敌人的移动
     */
    protected void move(){
        switch (state){
            case Constant.DIR_UP:
                y -=speed;
                if(y < RADIUS + GameFrame.titleHeight) y = RADIUS + GameFrame.titleHeight;
                break;
            case Constant.DIR_DOWN:
                y +=speed;
                if(y > GameFrame.mapHeight - RADIUS) y =  GameFrame.mapHeight - RADIUS;
                break;
            case Constant.DIR_LEFT:
                x -=speed;
                if(x < RADIUS) x = RADIUS;
                break;
            case Constant.DIR_RIGHT:
                x +=speed;
                if(x >  GameFrame.mapWidth - RADIUS) x = GameFrame.mapWidth - RADIUS;
                break;
        }
    }


    /**
     * 敌人移动ai
     */
    private void ai(){
        if( System.currentTimeMillis()-aiTime > Enemy.ENEMY_AI_INTERVAL[type]){
            aiTime=System.currentTimeMillis();

            int state = MyUtil.getRandomNumber(0,8);
            if(state == Constant.STATE_ATTACK_L || state ==Constant.STATE_STAND_L|| state == Constant.DIR_LEFT){
                setBoomDir(Constant.DIR_LEFT);
            }
            else if(state == Constant.STATE_ATTACK_R || state ==Constant.STATE_STAND_R|| state == Constant.DIR_RIGHT){
                setBoomDir(Constant.DIR_RIGHT);
            }else
                setBoomDir(state);

            setState(state);
        }

        if(state == Constant.STATE_ATTACK_L ||state == Constant.STATE_ATTACK_R ) fire();
        else if(Math.random()<Constant.ENEMY_FIRE_INTERVAL) fire();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
