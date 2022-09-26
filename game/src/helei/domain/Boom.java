package helei.domain;

import helei.game.GameFrame;
import helei.util.Constant;
import helei.util.MyUtil;

import java.awt.*;

/**
 * 飞行物类
 */
public class Boom {

     //默认速度
     public static final int DEFAULT_SPEED = 2;
     //炮弹半径
     public static final int RADIUS = 4;

     private int x,y;
     private int speed = DEFAULT_SPEED;
     private int dir;
     private int atk;
     private Color color;
     private boolean isVisible = true;

     public Boom() {}

     public Boom(int x, int y,  int dir, int atk, Color color ,int speed) {
         this.x = x;
         this.y = y;
         this.dir = dir;
         this.atk = atk;
         this.color = color;
         this.speed = speed;
     }

    /**
     * 绘制飞行物的方法
     * @return
     */
    public void draw(Graphics g){
        move();
        if(MyUtil.isInScreen(x,y) && isVisible){
            g.setColor(color);
            g.fillOval(MyUtil.getScreenPositionX(x)-RADIUS,MyUtil.getScreenPositionY(y)-RADIUS,
                    RADIUS<<1,RADIUS<<1);
        }
    }


    /**
     * 飞行物的移动
     */
    private void move(){

        switch (dir){
            case Constant.DIR_UP:
                y -= speed;
                if(y<0) isVisible=false;
                break;
            case Constant.DIR_DOWN:
                y += speed;
                if(y> GameFrame.mapHeight) isVisible=false;
                break;
            case Constant.DIR_LEFT:
                x -= speed;
                if(x<0) isVisible=false;
                break;
            case Constant.DIR_RIGHT:
                x += speed;
                if(x>GameFrame.mapWidth) isVisible=false;
                break;
        }
    }

    public void clear(){
        this.x = 0;
        this.y = 0;
        this.dir = 0;
        this.atk = 0;
        this.speed = 0;
        this.isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }
}
