package helei.domain;

import helei.util.MyUtil;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * 地图障碍物
 */
public class MapTile {

    public static int RADIUS = 0;

    private Image tileImg;
    private int x;
    private int y;

    public MapTile(int x,int y){
        this.x=x;
        this.y=y;
    }
    public MapTile() {
    }

    /**
     * 画地图块
     * @param g
     */
    public void draw(Graphics g){
        if(MyUtil.isInScreen(x+RADIUS*2,y+RADIUS*2)||MyUtil.isInScreen(x+RADIUS*2,y)
            ||MyUtil.isInScreen(x,y)||MyUtil.isInScreen(x,y+RADIUS*2)) {
            int screenX=MyUtil.getScreenPositionX(x);
            int screenY=MyUtil.getScreenPositionY(y);
            g.drawImage(tileImg, screenX, screenY, null);
        }
    }

    /**
     * 地图块与子弹的碰撞方法
     * @param booms
     * @return
     */
    public boolean isCollideBullet(List<Boom> booms){
        for (Boom boom : booms) {
            int boomX = boom.getX();
            int boomY = boom.getY();
            if(MyUtil.isCollide(x+RADIUS,y+RADIUS,RADIUS,boomX,boomY)){
                boom.setVisible(false);
            }
        }
        return false;
    }



    public Image getTileImg() {
        return tileImg;
    }

    public void setTileImg(Image tileImg) {
        this.tileImg = tileImg;
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

}
