package helei.domain;

import helei.game.GameFrame;
import helei.pool.MapTilePool;
import helei.util.Constant;
import helei.util.MyUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 地图类
 */
public class GameMap {
    //地图图片
    private static final Image[] mapImgs;
    //选择地图时的图片
    public static final Image[] mapSelectImgs;
    //地图图片类型数
    public static final int mapTypeNum;
    //障碍物图片
    private static final Image[] tileImgs;
    //障碍物图片类型数
    private static final int tileTypeNum;

    private static final Properties prop;

    static {
        prop = new Properties();
        try {
            prop.load(MapTile.class.getClassLoader().getResourceAsStream("properties/map.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //地图常量初始化
        mapTypeNum = Integer.parseInt(prop.getProperty("map_type_num"));
        mapSelectImgs = new Image[mapTypeNum];
        mapImgs = new Image[mapTypeNum];
        for(int i = 0; i < mapTypeNum; i++){
            mapSelectImgs[i]=Toolkit.getDefaultToolkit().createImage(prop.getProperty("map_select_"+i));
            mapImgs[i] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("map_img_"+i));
        }

        //障碍物常量初始化
        tileTypeNum = Integer.parseInt(prop.getProperty("tile_type_num"));
        tileImgs = new Image[tileTypeNum];
        for(int i=0;i<tileTypeNum;i++){
            tileImgs[i] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("tile_img_"+i));
        }
        //障碍物半径设置
        MapTile.RADIUS = Integer.parseInt(prop.getProperty("tile_radius"));

    }

    private int type;
    private Image background;
    private int width;
    private int height;
    private List<MapTile> tiles = new ArrayList<MapTile>();

    public GameMap(int type) {
        this.background=Toolkit.getDefaultToolkit().createImage(prop.getProperty("map_img_"+type));
        this.width = Integer.parseInt(prop.getProperty("map_img_width_"+type));
        this.height = Integer.parseInt(prop.getProperty("map_img_height_"+type));
        this.type = type;
        initMap();
    }
    public GameMap(int type,List<MapTile> tiles) {
        this.background=Toolkit.getDefaultToolkit().createImage(prop.getProperty("map_img_"+type));
        this.width = Integer.parseInt(prop.getProperty("map_img_width_"+type));
        this.height = Integer.parseInt(prop.getProperty("map_img_height_"+type));
        this.type = type;
        this.tiles = tiles;
    }
    public GameMap() {
    }

    /**
     * 初始化地图块
     */
    private void initMap(){
        int COUNT = Integer.parseInt(prop.getProperty("map_tile_num_"+type));

        for(int i=0;i<COUNT;i++) {
            MapTile tile = MapTilePool.get();
            //随机产生地图块
            int x = MyUtil.getRandomNumber(0, width);
            int y = MyUtil.getRandomNumber(0, height);
            int t = MyUtil.getRandomNumber(0,tileTypeNum);
            tile.setX(x);
            tile.setY(y);
            tile.setTileImg(GameMap.tileImgs[t]);
            tiles.add(tile);
        }
    }

    /**
     * 画地图
     * @param g
     */
    public void draw(Graphics g){
        //背景图
        g.drawImage(mapImgs[type], 0,0,Constant.FRAME_WIDTH,Constant.FRAME_HEIGHT
                , GameFrame.mapX,GameFrame.mapY,GameFrame.mapX+Constant.FRAME_WIDTH,
                GameFrame.mapY+Constant.FRAME_HEIGHT,null);

        //障碍物
        for (MapTile tile : tiles) {
            tile.draw(g);
        }
    }


    /**
     * 获取随机图片
     * @return
     */
    public static Image getRandomTileImg(){
        return tileImgs[MyUtil.getRandomNumber(0,tileTypeNum-1)];
    }

    public int getMapTypeNum() {
        return mapTypeNum;
    }

    public List<MapTile> getTiles() {
        return tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
