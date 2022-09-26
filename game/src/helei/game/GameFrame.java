package helei.game;

import helei.domain.*;
import helei.listener.MyKeyListener;
import helei.listener.MyWindowListener;
import helei.pool.BoomPool;
import helei.pool.MapTilePool;
import helei.util.Constant;
import helei.util.MyUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * 主窗口类
 */
public class GameFrame extends Frame{
    /**
     * 游戏名
     */
    private static  String GAME_NAME;

    /**
     * 背景图片
     */
    private static final Image[] backgrounds ;

    /**
     * 声音对象管理
     */
    public SoundManager soundManager;

    /**
     * 选择角色时展示的图片
     */
    private static final Map<String, Image> roleImgs;

    /**
     * 角色名字
     */
    private static final String [] roleNames;

    /**
     * 敌人最大数量
     */
    private static final int ENEMY_MAX_NUM;
    /**
     * 敌人出生时间
     */
    private static final int ENEMY_BORN_INTERVAL;

    /**
     * 屏幕的左上点在地图上的x坐标
     */
    public static int mapX;
    /**
     * 屏幕的左上点在地图上的y坐标
     */
    public static int mapY;
    /**
     * 地图高度
     */
    public static int mapHeight;
    /**
     * 地图宽度
     */
    public static int mapWidth;

    public static final int roleNum;
    private static final int roleImg_Height;
    private static final int roleImg_Width;

    //初始化图片等信息
    static {
        Properties prop = new Properties();
        try {
            prop.load(GameFrame.class.getClassLoader().getResourceAsStream("properties/game.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GAME_NAME =prop.getProperty("game_name");

        roleNum = Integer.parseInt(prop.getProperty("role_number"));
        roleImgs = new HashMap<>();
        roleNames = new String[roleNum];

        for(int i = 0 ; i < roleNum; i++)  {
            roleNames[i] = prop.getProperty("role" + i + "_name");

            String keyA = "role_"+i+"_img_a";
            Image valueA = Toolkit.getDefaultToolkit().createImage(prop.getProperty("role_" + i + "_img_a"));
            String keyB = "role_"+i+"_img_b";
            Image valueB = Toolkit.getDefaultToolkit().createImage(prop.getProperty("role_" + i + "_img_b"));
            roleImgs.put(keyA,valueA);
            roleImgs.put(keyB,valueB);
        }

        roleImg_Height =Integer.parseInt(prop.getProperty("role_img_height"));
        roleImg_Width =Integer.parseInt(prop.getProperty("role_img_width"));

        backgrounds=new Image[8];
        backgrounds[0] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("background_menu"));
        backgrounds[1] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("background_role"));
        backgrounds[2] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("background_help"));
        backgrounds[3] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("background_about"));
        backgrounds[4] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("background_over"));
        backgrounds[5] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("background_save_conf"));
        backgrounds[6] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("background_save_select"));
        backgrounds[7] = Toolkit.getDefaultToolkit().createImage(prop.getProperty("background_run_pause"));

        ENEMY_MAX_NUM = Integer.parseInt(prop.getProperty("enemy_max_number"));

        ENEMY_BORN_INTERVAL = Integer.parseInt(prop.getProperty("enemy_born_interval"));

        gameMap = new GameMap();

    }

    /**
     * 双缓冲底图
     */
    private BufferedImage bufImg = new BufferedImage(Constant.FRAME_WIDTH,Constant.FRAME_HEIGHT,BufferedImage.TYPE_4BYTE_ABGR);
    /**
     * 游戏的状态
     */
    public static int gameState;

    /**
     * 当前菜单的选项
     */
    public static int currentMenu;

    /**
     *  标题栏高度
     */
    public static int titleHeight;

    /**
     * 当前的角色类型
     */
    public static int currentRole = 0;

    /**
     * 退出界面当前的选择
     */
    public static int currentOver = 0;

    /**
     *  暂停页面的选择
     */
    public static int currentPause = 0;

    /**
     *  当前选择的地图编号
     */
    public static int currentMap = 0;

    /**
     * 保存页面选择是否保存
     */
    public static int currentSaveConf = 0;

    /**
     * 自己的角色
     */
    public static Me me ;

    /**
     * 敌人容器
     */
    public static List<Enemy> enemies = new ArrayList<>();
    /**
     * 当前敌人数量
     */
    public static int enemyNum = 0 ;
    /**
     * 结算时间计时器
     */
    private static long timer=0;

    /**
     * 地图对象
     */
    public static GameMap gameMap;

    public GameFrame(){

        soundManager = new SoundManager();
        soundManager.menuBgm();

        initFrame();
        initEventListener();
        //单独线程刷新窗口
        new Thread(()->{
            while(true){
                repaint();
                try {
                    Thread.sleep(Constant.REPAINT_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        makeEnemy();
    }

    /**
     * 保证同时之会有一个线程在产生敌人
     */
    private static Thread makeEnemyThread;

    /**
     * 创建生产敌人线程
     */
    private void makeEnemy(){
        if(makeEnemyThread!=null){
            makeEnemyThread.stop();
            makeEnemyThread = null;
        }

        //生产敌人线程
        makeEnemyThread = new Thread(()->{
            while(true){

                if(enemyNum < GameFrame.ENEMY_MAX_NUM){
                    enemyNum++;
                    enemies.add(Enemy.createEnemy());
                }
                try {
                    Thread.sleep(GameFrame.ENEMY_BORN_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //退出游戏状态，该线程自动销毁
                if(!(gameState == Constant.GAMESTATE_RUN||gameState==Constant.GAMESTATE_RUN_pause)) break;
            }
        });
        makeEnemyThread.start();
    }

    /**
     * 窗口初始化
     */
    public void initFrame(){
        setTitle(Constant.GAME_TITLE);
        setVisible(true);
        setSize(Constant.FRAME_WIDTH,Constant.FRAME_HEIGHT);
        setLocation(Constant.FRAME_x,Constant.FRAME_y);
        setResizable(false);

        titleHeight = getInsets().top;
    }

    /**
     * 重置游戏
     */
    public void resetGame(){
        me = null;

        //子弹归还池子
        for (Enemy enemy : enemies) {
            for (Boom boom : enemy.bullets) {
                BoomPool.theReturn(boom);
            }
        }

        //地图块对象返回池子
        List<MapTile> tiles = gameMap.getTiles();
        for (MapTile tile : tiles) {
            MapTilePool.theReturn(tile);
        }
        gameMap = null;
        enemies = null;
        enemyNum = 0;
        currentKill = 0;
    }

    /**
     * 开始新的游戏
     */
    public void newGame(){
        me =  new Me(Constant.FRAME_WIDTH/2,Constant.FRAME_HEIGHT/2,Constant.STATE_STAND_R,GameFrame.currentRole);
        me.setEnemy(false);

        //重置地图属性
        gameMap = new GameMap(currentMap);
        mapWidth = gameMap.getWidth();
        mapHeight = gameMap.getHeight();
        mapX = mapWidth/2 - Constant.FRAME_WIDTH/2;
        mapY = mapHeight/2 - Constant.FRAME_HEIGHT/2;


        enemies = new ArrayList<Enemy>();

        gameState = Constant.GAMESTATE_RUN;

        makeEnemy();

        soundManager.runBgm();
    }


    /**
     * 载入存档
     */
    public void loadOldGame(){

        Saver saver = saverList.get(currentSaveSelect);
        me = new Me(saver.getMeX(),saver.getMeY(),Constant.STATE_STAND_R,saver.getMeType());
        me.setHp(saver.getMeHp());
        me.isEnemy = false;

        enemies = MyUtil.loadEnemy(saver.getEnemyX(),saver.getEnemyY(),saver.getEnemyType(),saver.getEnemyHp());

        gameMap = new GameMap(saver.getMapType(),MyUtil.loadMapTile(saver.getTileX(),saver.getTileY()));
        mapWidth = gameMap.getWidth();
        mapHeight = gameMap.getHeight();

        mapX = saver.getMapX();
        mapY = saver.getMapY();

        gameState = Constant.GAMESTATE_RUN;
        currentKill = saver.getKillNumber();
        enemyNum = enemies.size()+currentKill;

        saverList = null;
        makeEnemy();

        soundManager.runBgm();
    }

    /**
     * 总共的存档数
     */
    public static int totalSaveNumber = 0;

    /**
     * 加载存档
     */
    public List<Saver> loadSave(){
        List<Saver> list = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("game\\save\\gamesave.txt"));
            list = (List<Saver>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {

        }
        if(list == null) list = new ArrayList<>();
        totalSaveNumber = list.size();
        this.saverList = list;
        return list;
    }

    /**
     * 保存游戏的方法，存档
     */
    public void saveGame() {
        Saver saver = new Saver();

        saver.setMapType(currentMap);
        saver.setMeX(me.getX());
        saver.setMeY(me.getY());
        saver.setMeHp(me.getHp());
        saver.setMeType(currentRole);
        saver.setMapX(mapX);
        saver.setMapY(mapY);
        List<Integer> enemyX = new ArrayList<>();
        List<Integer> enemyY = new ArrayList<>();
        List<Integer> enemyHp = new ArrayList<>();
        List<Integer> enemyType = new ArrayList<>();

        enemies.stream().forEach((enemy)->{
            enemyX.add(enemy.getX());
            enemyY.add(enemy.getY());
            enemyHp.add(enemy.getHp());
            enemyType.add(enemy.getType());
        });

        saver.setEnemyX(enemyX);
        saver.setEnemyY(enemyY);
        saver.setEnemyHp(enemyHp);
        saver.setEnemyType(enemyType);

        List<Integer> tileX = new ArrayList<>();
        List<Integer> tileY = new ArrayList<>();
        gameMap.getTiles().stream().forEach(mapTile -> {
            tileX.add(mapTile.getX());
            tileY.add(mapTile.getY());
        });

        saver.setTileX(tileX);
        saver.setTileY(tileY);
        saver.setSaveTime(System.currentTimeMillis());
        saver.setKillNumber(this.currentKill);

        //读取存档文件重新载入存档信息 saverList，
        loadSave();

        if(saverList == null) saverList = new ArrayList<>();
        saverList.add(saver);

        if(saverList.size() > 10) saverList.remove(0);

        saveListObject(saverList);
    }

    /**
     * 将存放存档信息 saver的对象集合持久化写入文件中
     * @param list
     */
    private void saveListObject(List<Saver> list){
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream("game\\save\\gamesave.txt"));
            oos.writeObject(list);
        } catch (IOException  e) {
            e.printStackTrace();
        }
        if(oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 删除当前的存档
     * @param c
     */
    public void deleteSave(int c){
        saverList.remove(c);
        totalSaveNumber = saverList.size();
        saveListObject(saverList);
    }

    /**
     * 每次刷新调用的方法，画下一帧的图
     * @param g
     */
    @Override
    public void update(Graphics g){
        //双缓冲的背景图
        Graphics gImg = bufImg.getGraphics();

        //判断是否结算
        if(timer!=0) {

            if(System.currentTimeMillis()-timer >= Constant.WAITE_TIME) {
                timer=0;
                gameState = Constant.GAMESTATE_MENU;
                soundManager.menuBgm();
            }
        }

        switch(gameState){
            case Constant.GAMESTATE_MENU:
                drawMenu(gImg);
                break;
            case Constant.GAMESTATE_ROLE:
                drawROLE(gImg);
                break;
            case Constant.GAMESTATE_HELP:
                drawHelp(gImg);
                break;
            case Constant.GAMESTATE_ABOUT:
                drawAbout(gImg);
                break;
            case Constant.GAMESTATE_RUN:
                drawRun(gImg);
                break;
            case Constant.GAMESTATE_OVER:
                drawOver(gImg);
                break;
            case Constant.GAMESTATE_MAP:
                drawMapSelect(gImg);
                break;
            case Constant.GAMESTATE_RUN_pause:
                drawRunPause(gImg);
                break;
            case Constant.GAMESTATE_SAVE_CONFIRM:
                drawSaveConfirm(gImg);
                break;
            case Constant.GAMESTATE_SAVE_SELECT:
                drawSaveSelect(gImg);
                break;
        }

        g.drawImage(bufImg,0,0,null);
    }



    /**
     * 当前选择的存档，
     */
    public static int currentSaveSelect = 0;

    /**
     * 当前的存档
     */
    public List<Saver> saverList = null;

    /**
     * 画选择存档界面
     * @param g
     */
    private void drawSaveSelect(Graphics g) {
        g.drawImage(backgrounds[Constant.GAMESTATE_SAVE_SELECT],0,0,null);
        g.setFont(Constant.GAMEOVER_FONT);
        g.drawString("请选择存档",Constant.FRAME_WIDTH/2-Constant.GAMEOVER_FONT.getSize()*2-Constant.GAMEOVER_FONT.getSize()/2,100);
        if(saverList == null) return;
        int x = Constant.FRAME_WIDTH/4;
        int y = Constant.FRAME_HEIGHT/4;
        int dir =40;
        g.setFont(Constant.MENU_FONT);

        for (int i = 0; i < totalSaveNumber; i++) {
            if(i == currentSaveSelect){
                g.setColor(Color.cyan);
            }else{
                g.setColor(Color.gray);
            }
            g.drawString("存档"+i+"时间： "+ MyUtil.parseDate(saverList.get(i).getSaveTime()),x,y+dir*i);
        }

        g.setColor(Color.white);

        g.drawString("按d删除存档",Constant.FRAME_WIDTH/2+Constant.FRAME_WIDTH/4,Constant.FRAME_HEIGHT-50);
    }

    /**
     * 画确认是否保存界面
     * @param g
     */
    private void drawSaveConfirm(Graphics g) {
        g.drawImage(backgrounds[Constant.GAMESTATE_SAVE_CONFIRM],0,0,null);

        g.setFont(Constant.MENU_FONT);
        g.setColor(Color.YELLOW);
        int x=Constant.FRAME_WIDTH/2-Constant.MENU_FONT.getSize()*Constant.OVER_TITLE.length()/2;
        int y=Constant.FRAME_HEIGHT/2;
        g.drawString("是否保存",x,y);
        int dir = g.getFont().getSize();
        String [] tip = Constant.CONFIRM;

        for(int i = 0; i < tip.length ;i++){
            if(i == currentSaveConf){
                g.setColor(Color.ORANGE);
            }else{
                g.setColor(Color.WHITE);
            }
            g.drawString(tip[i],x+7*dir*i,y+dir*2);
        }
    }


    /**
     * 暂停
     */
    private void drawRunPause(Graphics g) {

        g.drawImage(backgrounds[Constant.GAMESTATE_RUN_pause],0,0,null);
        g.setFont(Constant.MENU_FONT);
        g.setColor(Color.YELLOW);
        int x=Constant.FRAME_WIDTH/2-Constant.MENU_FONT.getSize()*Constant.OVER_TITLE.length()/2;
        int y=Constant.FRAME_HEIGHT/2;
        g.drawString(Constant.PAUSE_TITLE,x,y);
        int dir = g.getFont().getSize();
        String [] tip = Constant.CONFIRM;

        for(int i = 0; i < tip.length ;i++){
            if(i == currentPause){
                g.setColor(Color.ORANGE);
            }else{
                g.setColor(Color.WHITE);
            }
            g.drawString(tip[i],x+7*dir*i,y+dir*2);
        }
    }


    /**
     * 绘制地图选择界面
     * @param g
     */
    private void drawMapSelect(Graphics g) {

        g.drawImage(GameMap.mapSelectImgs[currentMap],0,0,null);
        g.setColor(Color.YELLOW);
        g.setFont(Constant.MENU_FONT);
        g.drawString("请选择地图",Constant.FRAME_WIDTH/2-Constant.MENU_FONT.getSize()*3,Constant.FRAME_HEIGHT>>3);

        for(int i=0; i < gameMap.getMapTypeNum() ; i++){
            if(i == currentMap){
                g.setColor(Color.GREEN);
                g.setFont(Constant.MENU_FONT);
                g.drawString("地图"+(i+1),Constant.FRAME_WIDTH/2-Constant.MENU_FONT.getSize()*2,Constant.FRAME_HEIGHT>>1);
            }
        }
    }


    /**
     * 游戏帮助菜单的绘制
     * @param
     */
    private void drawHelp(Graphics g) {
        g.drawImage(backgrounds[gameState],0,0,null);
        g.setColor(Color.BLACK);
        g.setFont(Constant.MENU_FONT);
        g.drawString(Constant.HELP_MASSAGE,Constant.FRAME_WIDTH/2-g.getFont().getSize()*Constant.HELP_MASSAGE.length()/2,Constant.FRAME_HEIGHT/2);
        g.drawString(Constant.HELP_ROLL,Constant.FRAME_WIDTH/2-g.getFont().getSize()*Constant.HELP_ROLL.length()/2,Constant.FRAME_HEIGHT/2+50);
        g.drawString(Constant.HELP_TIP,Constant.FRAME_WIDTH/2-g.getFont().getSize()*Constant.HELP_TIP.length()/2,Constant.FRAME_HEIGHT/2+100);

    }


    /**
     * 计时器控制角色语音间隔
     */
    private long voiceTimer = 0;
    /**
     * 游戏进行的绘制
     * @param g
     */
    private void drawRun(Graphics g) {

        //控制游戏中角色声音播放间隔
        if(voiceTimer==0){
            soundManager.playRunVoice(me.type);
            voiceTimer=System.currentTimeMillis();
        }else if(System.currentTimeMillis() - voiceTimer > 10000){
            voiceTimer=0;
        }

        //画地图
        gameMap.draw(g);

        //画敌人
        drawEnemies(g);

        bulletCollideTank();

        bulletCollideMapTile();

        //胜利
        if(timer==0 && enemyNum == GameFrame.ENEMY_MAX_NUM && enemies.size() ==0) {
            me.setState(Constant.STATE_VEC);
            timer = System.currentTimeMillis();
            soundManager.playRoleVictoryVoice(me.type);
        }
        //失败
        else if(timer==0&&me.getState() == Constant.STATE_DIE){
            timer = System.currentTimeMillis();
            soundManager.playRoleDieVoice(me.type);
        }

        me.draw(g);

        drawRoleVicOrDie(g);

        drawCurrentKill(g);
    }

    /**
     * 记录当前击杀
     */
    private int currentKill = 0;
    /**
     * 画当前击杀了多少，剩余多少
     * @param g
     */
    private void drawCurrentKill(Graphics g){
        g.setFont(Constant.GAMEOVER_FONT);
        g.setColor(Color.black);
        g.drawString("当前击杀了"+currentKill+"名敌人，剩余"+(GameFrame.ENEMY_MAX_NUM-currentKill),Constant.FRAME_WIDTH/2-g.getFont().getSize()*6,100);
    }


    /**
     * 画游戏结算时的方法
     * @param g
     */
    private void drawRoleVicOrDie(Graphics g){
        if(me.getState() == Constant.STATE_VEC) {
            g.setFont(Constant.GAMEOVER_FONT);
            g.setColor(Color.YELLOW);
            g.drawString("胜 利", Constant.FRAME_WIDTH/2-48, Constant.FRAME_HEIGHT / 2);
        }
        if(me.getState() == Constant.STATE_DIE){
            g.setFont(Constant.GAMEOVER_FONT);
            g.setColor(Color.RED);
            g.drawString("失 败",Constant.FRAME_WIDTH/2-48,Constant.FRAME_HEIGHT/2);
        }
    }

    /**
     * 画敌人
     * @param g
     */
    private void drawEnemies(Graphics g){
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if(enemy.getHp()<=0) {
                enemies.remove(i);
                currentKill++;
            }
        }
    }

    /**
     * 子弹与人碰撞
     */
    private void bulletCollideTank(){
        for (Enemy enemy : enemies) {
            enemy.collideBullets(me.getBullets());
        }
        for (Enemy enemy : enemies) {
            me.collideBullets(enemy.getBullets());
        }

    }

    /**
     * 所有子弹和地图块的碰撞
     */
    private void bulletCollideMapTile(){
        List<MapTile> tiles = gameMap.getTiles();
        for (MapTile tile : tiles) {
            //自己的子弹和地图块碰撞
            tile.isCollideBullet(me.getBullets());
            //自己和地图块碰撞
            if(me.isCollideTile(tile)){
                me.back();
            }
            //敌人的子弹和地图块碰撞
            for (Enemy enemy : enemies) {
                //敌人子弹和块碰撞
                tile.isCollideBullet(enemy.getBullets());
                //敌人和块碰撞
                if(enemy.isCollideTile(tile)){
                    enemy.back();
                }
            }
        }
    }

    /**
     * 关于界面
     * @param g
     */
    private void drawAbout(Graphics g) {
        g.drawImage(backgrounds[gameState],0,0,null);
        g.setColor(Color.WHITE);
        g.setFont(Constant.MENU_FONT);
        int x = Constant.FRAME_WIDTH/2;
        int y = Constant.FRAME_HEIGHT/2;
        int dir = 2*Constant.MENU_FONT.getSize();
        g.drawString(Constant.ABOUT_MASSAGE,x-Constant.MENU_FONT.getSize()*Constant.ABOUT_MASSAGE.length()/2,y);
        g.drawString(Constant.ABOUT_MOREPOINT,x-Constant.MENU_FONT.getSize()*Constant.ABOUT_MOREPOINT.length()/2,y+dir);
        g.drawString(Constant.ABOUT_TIP,x-Constant.MENU_FONT.getSize()*Constant.ABOUT_TIP.length()/2,y+2*dir);
        g.drawString(Constant.ABOUT_PHONE,x-Constant.MENU_FONT.getSize()*Constant.ABOUT_PHONE.length()/2,y+3*dir);
    }

    /**
     * 绘制角色展示
     * @param g
     */
    private void drawROLE(Graphics g) {
        g.drawImage(backgrounds[gameState],0,0,null);
        g.setFont(Constant.MENU_FONT);
        int dir = Constant.FRAME_WIDTH/roleNum;
        for(int i=0;i<roleNum;i++) {
            if(i==currentRole) {
                g.drawImage(roleImgs.get("role_"+i+"_img_b"), dir * i+roleImg_Width/2-20, Constant.FRAME_HEIGHT >> 1, null);
                g.setColor(Color.ORANGE);
            }else {
                g.drawImage(roleImgs.get("role_"+i+"_img_a"),dir * i, Constant.FRAME_HEIGHT >> 2, null);
                g.setColor(Color.WHITE);
            }
            g.drawString(roleNames[i],dir * i+roleImg_Width/2,(Constant.FRAME_HEIGHT >> 2)+roleImg_Height );
        }
    }

    /**
     * 结束页面
     * @param g
     */
    private void drawOver(Graphics g) {
        g.drawImage(backgrounds[gameState],0,0,null);
        g.setColor(Color.CYAN);
        g.setFont(Constant.MENU_FONT);
        int x=Constant.FRAME_WIDTH/2-Constant.MENU_FONT.getSize()*Constant.OVER_TITLE.length()/2;
        int y=Constant.FRAME_HEIGHT/2;
        g.drawString(Constant.OVER_TITLE,x,y);

        int dir = g.getFont().getSize();
        String [] tip = Constant.CONFIRM;
        for(int i = 0; i < tip.length ;i++){
            if(i == currentOver){
                g.setColor(Color.ORANGE);
            }else{
                g.setColor(Color.WHITE);
            }
            g.drawString(tip[i],x+7*dir*i,y+dir*2);
        }
    }


    /**
     * 绘制菜单
     * @param g
     */
    private void drawMenu(Graphics g){
        g.setFont(Constant.MENU_FONT);
        g.drawImage(backgrounds[gameState],0,0,null );

        final int STR_WIDTH = 76;
        int x = 50;
        int y = Constant.FRAME_HEIGHT -50 ;
        final int DIS =Constant.FRAME_WIDTH/Constant.MENUS.length;
        g.setColor(Color.black);
        g.fillRect(0,y-50,Constant.FRAME_WIDTH,100);
        g.setColor(Color.white);
        for (int i = 0; i < Constant.MENUS.length; i++) {
            if(i==currentMenu){
                g.setColor(Color.ORANGE);
            }else{
                g.setColor(Color.WHITE);
            }
            g.drawString(Constant.MENUS[i],x+DIS*i,y);
        }


        //游戏名
        x = Constant.FRAME_WIDTH/2 - Constant.GAMENAME_FONT.getSize() * (GAME_NAME.length()/2)
                - GAME_NAME.length()%2*Constant.GAMENAME_FONT.getSize()/2;
        y = Constant.FRAME_HEIGHT/4;

        g.setFont(Constant.GAMENAME_FONT);
        g.setColor(Color.pink);
        g.drawString(GAME_NAME,x,y);
    }

    /**
     * 注册监听
     */
    private void initEventListener(){
        addWindowListener(new MyWindowListener());
        addKeyListener(new MyKeyListener(this));
    }

}
