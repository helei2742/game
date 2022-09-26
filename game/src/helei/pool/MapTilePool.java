package helei.pool;

import helei.domain.MapTile;

import java.util.ArrayList;
import java.util.List;

public class MapTilePool {

    //默认池子大小
    public static final int DEFAULT_POOL_SIZE = 30;
    //最大大小
    public static final int POOL_MAX_SIZE = 70;

    private static List<MapTile> pool = new ArrayList<MapTile>();

    static {
        for(int i = 0; i < DEFAULT_POOL_SIZE ;i++){
            pool.add(new MapTile());
        }
    }

    /**
     * 池子获取对象
     */
    public static MapTile get(){
        MapTile tile = null;
        if(pool.size() == 0){
            tile = new MapTile();
            pool.add(tile);
        }else{
            tile = pool.remove(0);
        }
        return tile;
    }

    /**
     * 对象返回池子
     * @param tile
     */
    public static void theReturn(MapTile tile){
        if(pool.size() == POOL_MAX_SIZE){
            return;
        }
        pool.add(tile);
    }
}
