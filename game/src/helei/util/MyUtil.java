package helei.util;

import helei.domain.Enemy;
import helei.domain.GameMap;
import helei.domain.MapTile;
import helei.game.GameFrame;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyUtil {
    public static Random rd = new Random();

    public static boolean getRandomBool(){
        return rd.nextBoolean();
    }

    public static int getRandomNumber(int min, int max){
        return (int)(Math.random()*(max-min)+min);
    }

    public static Color getRandomColor(){
        int r = getRandomNumber(0,256);
        int b = getRandomNumber(0,256);
        int g = getRandomNumber(0,256);
        return new Color(r,b,g);
    }

    public static boolean isCollide(int rectX,int rectY,int radius,int pointX,int pointY){
        int disX = Math.abs(rectX-pointX);
        int disY = Math.abs(rectY-pointY);
        return disX < radius && disY < radius;
    }

    public static String getStateString(int state){
        if(state == Constant.DIR_UP ||state == Constant.DIR_DOWN ||
                state == Constant.DIR_LEFT||state == Constant.DIR_RIGHT )
        {
            return "move";
        }
        if(state == Constant.STATE_ATTACK_L||state == Constant.STATE_ATTACK_R){
            return "attack";
        }
        if(state == Constant.STATE_STAND_L||state == Constant.STATE_STAND_R){
            return "stand";
        }
        if(state == Constant.STATE_DIE){
          return "die" ;
        }
        if(state == Constant.STATE_VEC){
            return "victory";
        }
        return null;
    }

    private static final String[] NAMES = {
        "阿三","小七","琪亚娜","芽衣","喵内","莲华","智乃","心爱","伊雷娜","喵帕斯","初音"

    };

    private static final String[] MODIFIY={
        "萌萌的","高高的","傻傻的","帅气的","顽皮的","狡猾的","凌厉的"
    };

    public static String getRandomName(){
        return MODIFIY[getRandomNumber(0, MODIFIY.length)]+NAMES[getRandomNumber(0, NAMES.length)];
    }

    public static boolean isInScreen(int x, int y){
        int x1 = x- GameFrame.mapX;
        int y1 = y- GameFrame.mapY;
        return x1>=0&&x1<=Constant.FRAME_WIDTH&&y1>=0&&y1<=Constant.FRAME_HEIGHT;
    }

    public static int getScreenPositionX(int x){
        return x- GameFrame.mapX;
    }
    public static int getScreenPositionY(int y){
        return y- GameFrame.mapY;
    }

    public static String parseDate(long t){
        return new SimpleDateFormat("yyyy年MM月dd日hh时mm分ss秒").format(t);
    }

    public static List<Enemy> loadEnemy( List<Integer> ex,List<Integer> ey,List<Integer> et,List<Integer> eh){
        List<Enemy> list = new ArrayList<>();
        int len = ex.size();
        for(int i = 0 ; i< len ;i++){
            Enemy e = new Enemy(ex.get(i),ey.get(i),Constant.STATE_STAND_R,et.get(i));
            e.setHp(eh.get(i));
            list.add(e);
        }
        return list;
    }
    public static List<MapTile> loadMapTile(List<Integer> tx,List<Integer> ty){
        List<MapTile> list = new ArrayList<>();
        int len = tx.size();
        for(int i = 0; i < len ; i++){
            MapTile tile = new MapTile(tx.get(i), ty.get(i));
            tile.setTileImg(GameMap.getRandomTileImg());
            list.add(tile);
        }
        return list;
    }
}
