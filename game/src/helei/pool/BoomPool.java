package helei.pool;

import helei.domain.Boom;
import helei.domain.MapTile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoomPool {

        //默认池子大小
        public static final int DEFAULT_POOL_SIZE = 50;
        //最大大小
        public static final int POOL_MAX_SIZE = 300;

        private static List<Boom> pool = new ArrayList<Boom>();

        static {
            for(int i = 0; i < DEFAULT_POOL_SIZE ;i++){
                pool.add(new Boom());
            }
        }

        /**
         * 池子获取对象
         */
        public static Boom get(){
            Boom boom = null;
            if(pool.size() == 0){
                boom = new Boom();
                pool.add(boom);
            }else{
                boom = pool.remove(0);
            }
            return boom;
        }

    /**
     * 获取带参数的子弹
     * @param x
     * @param y
     * @param dir
     * @param atk
     * @param color
     * @param speed
     * @return
     */
    public static Boom get(int x, int y, int dir, int atk, Color color ,int speed){
        Boom boom = get();
        if(boom == null) return null;
        boom.setX(x);
        boom.setY(y);
        boom.setDir(dir);
        boom.setAtk(atk);
        boom.setColor(color);
        boom.setSpeed(speed);
        return boom;
    }

        /**
         * 对象返回池子
         * @param boom
         */
        public static void theReturn(Boom boom){
            if(pool.size() == POOL_MAX_SIZE){
                return;
            }
            boom.clear();
            pool.add(boom);
        }
}
