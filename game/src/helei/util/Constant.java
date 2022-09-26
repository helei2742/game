package helei.util;

import java.awt.*;

public class Constant {

    //窗口标题
    public static final String GAME_TITLE = "2019141460347李鹄宏";
    //页面宽
    public static final int FRAME_WIDTH = 1080;
    //页面高
    public static final int FRAME_HEIGHT = 760;
    //开始菜单文字位置
    public static final int FRAME_x = 1024-FRAME_WIDTH>>1;
    public static final int FRAME_y = 768 - 700>>1;

    //游戏状态
    public static final int GAMESTATE_MENU = 0;
    public static final int GAMESTATE_ROLE = 1;
    public static final int GAMESTATE_HELP = 2;
    public static final int GAMESTATE_ABOUT = 3;
    public static final int GAMESTATE_OVER = 4;
    public static final int GAMESTATE_SAVE_CONFIRM = 5;
    public static final int GAMESTATE_SAVE_SELECT = 6;
    public static final int GAMESTATE_RUN_pause = 7;

    public static final int GAMESTATE_RUN = 8;
    public static final int GAMESTATE_MAP = 9;


    //菜单选项
    public static final String[] MENUS = {
        "开始游戏","选择存档","角色选择","游戏帮助","游戏关于","退出游戏"
    };

    //帮助菜单的提示信息
    public static final String HELP_MASSAGE="在初始菜单选中选择人物后返回，选择开始游戏即可开始(按键盘任意位置返回)";
    public static final String HELP_ROLL = "消灭所有敌人取得胜利";
    public static final String HELP_TIP = "可在根目录下的properties文件或util下Constant中更改对应属性";

    //关于菜单的提示信息
    public static final String ABOUT_MASSAGE="四川大学2019141460347李鹄宏";
    public static final String ABOUT_MOREPOINT="制作不易，多给点分吧";
    public static final String ABOUT_TIP = "本作品不用于任何商业用途，所选图片均来自手游少女前线和p站，如有侵权请联系作者删除";
    public static final String ABOUT_PHONE = "电话：19822901829";

    //退出菜单提示
    public static final String OVER_TITLE = "指挥官真的要走了吗";
    //游戏暂停提示
    public static final String PAUSE_TITLE = "是否退出游戏";

    public static final String [] CONFIRM={"确定","取消"};

    //刷新时间
    public static final int REPAINT_INTERVAL = 15;
    //结算时间
    public static final int WAITE_TIME = 5000;
    //显示的字体
    public static final Font MENU_FONT = new Font("宋体",Font.BOLD,24);
    public static final Font NAME_FONT = new Font("微软雅黑",Font.PLAIN,12);
    public static final Font GAMEOVER_FONT = new Font("微软雅黑",Font.PLAIN,48);
    public static final Font GAMENAME_FONT = new Font("宋体",Font.PLAIN,76);

    //四个方向移动属性，以及站立属性和攻击等属性
    public static final int DIR_UP = 0;
    public static final int DIR_DOWN = 1;
    public static final int DIR_LEFT = 2;
    public static final int DIR_RIGHT = 3;
    public static final int STATE_STAND_L = 4;
    public static final int STATE_STAND_R = 5;
    public static final int STATE_ATTACK_L =6;
    public static final int STATE_ATTACK_R =7;
    public static final int STATE_DIE = 8;
    public static final int STATE_VEC = 9;

    //敌人每个状态都可能开火的概率
    public static final double ENEMY_FIRE_INTERVAL = 0.05;
}
