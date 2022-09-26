package helei.domain;

import helei.game.GameFrame;
import helei.pool.BoomPool;
import helei.util.Constant;
import helei.util.MyUtil;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 坦克类
 */
public class People implements Serializable {
    //半径
    public static final int RADIUS = 40;
    //默认速度，每帧
    public static final int DEFAULT_SPEED = 4;
    //默认血量
    public static final int DEFAULT_HP = 1000;
    //默认攻击力
    public static final int DEFAULT_ATK = 100;
    //默认攻速
    public static final int DEFAULT_ATK_SPEED = 500;

    protected int maxHp = DEFAULT_HP;
    //当前坐标
    protected int x,y;
    //当前血量
    protected int hp = DEFAULT_HP;
    //当前攻击力
    protected int atk = DEFAULT_ATK;
    //当前速度
    protected int speed = DEFAULT_SPEED;
    //子弹颜色
    protected Color color ;
    //当前状态
    protected int state = Constant.STATE_STAND_R;
    //子弹方向
    protected int boomDir;
    //子弹速度
    protected int boomSpeed;
    //攻击速度
    protected int atkSpeed = DEFAULT_ATK_SPEED;
    //所射出的子弹
    public List<Boom> bullets = new ArrayList<Boom>();

    protected String name;

    public boolean isEnemy = true;

    public People(int x, int y, int state) {
        this.x = x;
        this.y = y;
        this.state = state;
        this.color = MyUtil.getRandomColor();
    }

    //子弹发射记时，控制攻速
    long timer;

    /**
     * 发射子弹
     */
    public void fire(){
        //控制攻速
        if(timer == 0){
            timer=System.currentTimeMillis();
        }else{
            if(System.currentTimeMillis()-timer >= atkSpeed){
                timer = 0;
            }
            else return;
        }

        int boomX = x;
        int boomY = y;
        switch (boomDir){
            case Constant.DIR_UP:
            case Constant.DIR_RIGHT:
                boomY += 2;
                boomX += RADIUS;
                state = Constant.STATE_ATTACK_R;
                break;
            case Constant.DIR_DOWN:
            case Constant.DIR_LEFT:
                boomX -=RADIUS;
                boomY +=2;
                state = Constant.STATE_ATTACK_L;
                break;
        }

        //自己的子弹，将相对屏幕坐标转换为相对地图坐标
        if(!isEnemy){
            boomX +=GameFrame.mapX;
            boomY +=GameFrame.mapY;
        }

        Boom boom = BoomPool.get(boomX, boomY, boomDir, atk, color ,boomSpeed);

        bullets.add(boom);
    }

    /**
     * 绘制 名字
     * @param g
     */
    protected void drawName(Graphics g){
        g.setColor(color);
        g.setFont(Constant.NAME_FONT);
        if(isEnemy)
        g.drawString(name,MyUtil.getScreenPositionX(x)-RADIUS+17, MyUtil.getScreenPositionY(y)-60);
        else{
            g.drawString(name,x-RADIUS+17, y-60);
        }
    }

    /**
     * 绘制发射的子弹
     */
    protected void drawBoom(Graphics g){
        for (Boom bullet : bullets) {
            bullet.draw(g);
        }
        //不可见的子弹归还池子
        for (int i = 0; i < bullets.size(); i++) {
             Boom boom = bullets.get(i);
            if(!boom.isVisible()){
                bullets.remove(i);
                BoomPool.theReturn(boom);
            }
        }
    }

    /**
     * 子弹碰撞判定
     * @param bullets 与这些子弹做比较
     */
    public void collideBullets(List<Boom> bullets){
        for (Boom bullet : bullets) {
            int screamX =isEnemy?MyUtil.getScreenPositionX(x):x;
            int screamY =isEnemy?MyUtil.getScreenPositionY(y):y;
            int boomScreamX = MyUtil.getScreenPositionX(bullet.getX());
            int boomScreamY = MyUtil.getScreenPositionY(bullet.getY());
            if(MyUtil.isCollide(screamX,screamY,RADIUS,boomScreamX,boomScreamY)){
                bullet.setVisible(false);
                this.hp -= bullet.getAtk();
            }
        }
    }

    /**
     * 地图块和当前人的碰撞方法
     * 从tile中提取8个点，判断8个点是否有任何一个和当前人有碰撞
     * @param tile
     * @return
     */
    public boolean isCollideTile(MapTile tile){
        int tileX = tile.getX();
        int tileY = tile.getY();
        int realX = x;
        int realY = y;
        if(!isEnemy){
           realX += GameFrame.mapX;
           realY += GameFrame.mapY;
        }
        //左上点
        boolean collide = MyUtil.isCollide(realX, realY, RADIUS, tileX, tileY);
        if(collide &&( state==Constant.DIR_RIGHT || state == Constant.DIR_DOWN )) return true;
        //中上点
        tileX += MapTile.RADIUS;
        collide = MyUtil.isCollide(realX, realY, RADIUS, tileX, tileY);
        if(collide && state == Constant.DIR_DOWN ) return true;
        //右上点
        tileX += MapTile.RADIUS;
        collide = MyUtil.isCollide(realX, realY, RADIUS, tileX, tileY);
        if(collide &&( state==Constant.DIR_LEFT || state == Constant.DIR_DOWN )) return true;
        //右中点
        tileY += MapTile.RADIUS;
        collide = MyUtil.isCollide(realX, realY, RADIUS, tileX, tileY);
        if(collide && state==Constant.DIR_LEFT ) return true;
        //右下点
        tileY += MapTile.RADIUS;
        collide = MyUtil.isCollide(realX, realY, RADIUS, tileX, tileY);
        if(collide && ( state==Constant.DIR_LEFT || state == Constant.DIR_UP )) return true;
        //中下点
        tileX -= MapTile.RADIUS;
        collide = MyUtil.isCollide(realX, realY, RADIUS, tileX, tileY);
        if(collide &&state == Constant.DIR_UP ) return true;
        //左下点
        tileX -= MapTile.RADIUS;
        collide = MyUtil.isCollide(realX, realY, RADIUS, tileX, tileY);
        if(collide && ( state==Constant.DIR_RIGHT || state == Constant.DIR_UP )) return true;
        //左中点
        tileY -= MapTile.RADIUS;
        collide = MyUtil.isCollide(realX, realY, RADIUS, tileX, tileY);
        if(collide && state==Constant.DIR_RIGHT) return true;

        return false;
    }


    /**
     * 回退方法，人物和块碰撞后回退
     */
    public void back(){
        if(this.state == Constant.DIR_UP) this.y +=this.speed;
        if(this.state == Constant.DIR_DOWN) this.y -=this.speed;
        if(this.state == Constant.DIR_LEFT) this.x +=this.speed;
        if(this.state == Constant.DIR_RIGHT) this.x -=this.speed;
    }

    public int getBoomDir() {
        return boomDir;
    }

    public void setBoomDir(int boomDir) {
        this.boomDir = boomDir;
    }

    public List<Boom> getBullets() {
        return bullets;
    }

    public void setBullets(List<Boom> bullets) {
        this.bullets = bullets;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public void setEnemy(boolean enemy) {
        isEnemy = enemy;
    }

    //血条内部类
    class BloodBar{
        private static final int BAR_LENGTH = 50;
        private static final int BAR_HEIGHT = 5;
        public void draw(Graphics g){
            int enemyX = MyUtil.getScreenPositionX(x);
            int enemyY = MyUtil.getScreenPositionY(y);

            g.setColor(Color.WHITE);
            g.fillRect(enemyX-RADIUS-1+17,enemyY-RADIUS-BAR_HEIGHT*2-1,BAR_LENGTH+2,BAR_HEIGHT+2);

            g.setColor(Color.gray);
            g.fillRect(enemyX-RADIUS+17,enemyY-RADIUS-BAR_HEIGHT*2,BAR_LENGTH,BAR_HEIGHT);

            g.setColor(Color.RED);
            g.fillRect(enemyX-RADIUS+17,enemyY-RADIUS-BAR_HEIGHT*2,BAR_LENGTH*hp/maxHp,BAR_HEIGHT);
        }
        public void drawMe(Graphics g){

            g.setColor(Color.WHITE);
            g.fillRect(x-RADIUS-1+17,y-RADIUS-BAR_HEIGHT*2-1,BAR_LENGTH+2,BAR_HEIGHT+2);

            g.setColor(Color.gray);
            g.fillRect(x-RADIUS+17,y-RADIUS-BAR_HEIGHT*2,BAR_LENGTH,BAR_HEIGHT);

            g.setColor(Color.RED);
            g.fillRect(x-RADIUS+17,y-RADIUS-BAR_HEIGHT*2,BAR_LENGTH*hp/maxHp,BAR_HEIGHT);
        }
    }
}
