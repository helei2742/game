package helei.listener;

import helei.domain.GameMap;
import helei.domain.Me;
import helei.domain.People;
import helei.domain.Saver;
import helei.game.GameFrame;
import helei.util.Constant;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class MyKeyListener extends KeyAdapter {
    private GameFrame gameFrame;
    private long voiceTimer=0;

    public MyKeyListener(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }
    public MyKeyListener() {
    }

    //按下
    @Override
    public void keyPressed(KeyEvent e){
        int keyCode = e.getKeyCode();
        switch(GameFrame.gameState){
            case Constant.GAMESTATE_MENU:
                keyEventPressedMenu(keyCode);
                break;
            case Constant.GAMESTATE_ROLE:
                keyEventPressedRole(keyCode);
                break;
            case Constant.GAMESTATE_HELP:
                keyEventPressedHelp(keyCode);
                break;
            case Constant.GAMESTATE_ABOUT:
                keyEventPressedAbout(keyCode);
                break;
            case Constant.GAMESTATE_RUN:
                keyEventPressedRun(keyCode);
                break;
            case Constant.GAMESTATE_OVER:
                keyEventPressedOver(keyCode);
                break;
            case Constant.GAMESTATE_MAP:
                keyEventPressedMap(keyCode);
                break;
            case Constant.GAMESTATE_RUN_pause:
                keyEventPressedPause(keyCode);
                break;
            case Constant.GAMESTATE_SAVE_CONFIRM:
                keyEventPressedSaveConf(keyCode);
                break;
            case Constant.GAMESTATE_SAVE_SELECT:
                keyEventPressedSaveSelect(keyCode);
                break;
        }
    }

    /**
     * 选择存档的按键方法
     * @param keyCode
     */
    private void keyEventPressedSaveSelect(int keyCode) {
        switch (keyCode){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if(--GameFrame.currentSaveSelect < 0) GameFrame.currentSaveSelect = GameFrame.totalSaveNumber - 1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if(++GameFrame.currentSaveSelect >= GameFrame.totalSaveNumber) GameFrame.currentSaveSelect =0;
                break;
            case KeyEvent.VK_ENTER:
                loadOldGame();
                break;
            case KeyEvent.VK_D:
                deleteSave(GameFrame.currentSaveSelect);
                break;
            case KeyEvent.VK_ESCAPE:
                toMenu();
                break;
        }
    }

    /**
     * 删除存档
     */
    private void deleteSave(int current) {
        gameFrame.deleteSave(current);
    }

    /**
     * 载入存档开始游戏
     */
    private void loadOldGame() {
        if(gameFrame.saverList == null) return;
        gameFrame.resetGame();
        gameFrame.loadOldGame();
        GameFrame.gameState = Constant.GAMESTATE_RUN;
    }

    /**
     * 选择是否保存的页面的按键方法
     * @param keyCode
     */
    private void keyEventPressedSaveConf(int keyCode) {
        switch (keyCode){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
                if(--GameFrame.currentSaveConf < 0) GameFrame.currentSaveConf = 1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_A:
                if(++GameFrame.currentSaveConf >= 2) GameFrame.currentSaveConf =0;
                break;
            case KeyEvent.VK_ENTER:
                if(GameFrame.currentSaveConf == 0) saveGame();
                toMenu();
                break;
            case KeyEvent.VK_ESCAPE:
                GameFrame.gameState = Constant.GAMESTATE_RUN;
                break;
        }
    }

    /**
     * 保存游戏方法
     */
    private void saveGame(){
        gameFrame.saveGame();
        GameFrame.gameState = Constant.GAMESTATE_MENU;
    }



    /**
     * 游戏暂停的按键事件
     * @param keyCode
     */
    private void keyEventPressedPause(int keyCode) {
        switch (keyCode){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
                if(--GameFrame.currentPause < 0) GameFrame.currentPause = 1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_A:
                if(++GameFrame.currentPause >= 2) GameFrame.currentPause =0;
                break;
            case KeyEvent.VK_ENTER:
                if(GameFrame.currentPause == 0)
                    GameFrame.gameState = Constant.GAMESTATE_SAVE_CONFIRM;
                else
                    GameFrame.gameState = Constant.GAMESTATE_RUN;
                break;
            case KeyEvent.VK_ESCAPE:
                GameFrame.gameState = Constant.GAMESTATE_RUN;
                break;
        }
    }

    /**
     * 地图选择的按键事件
     * @param keyCode
     */
    private void keyEventPressedMap(int keyCode) {
        switch (keyCode){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
                if(--GameFrame.currentMap < 0) GameFrame.currentMap = GameMap.mapTypeNum-1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_A:
                if(++GameFrame.currentMap >= GameMap.mapTypeNum) GameFrame.currentMap =0;
                break;
            case KeyEvent.VK_ENTER:
                newGame();
                break;
            case KeyEvent.VK_ESCAPE:
                toMenu();
                break;
        }
    }

    /**
     * 帮助菜单的按键事件
     * @param keyCode
     */
    private void keyEventPressedHelp(int keyCode) {
        toMenu();
    }

    /**
     * 菜单的点击事件
     * @param keyCode
     */
    private void keyEventPressedMenu(int keyCode) {
        switch (keyCode){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                if(--GameFrame.currentMenu<0) GameFrame.currentMenu = Constant.MENUS.length-1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                if(++GameFrame.currentMenu >= Constant.MENUS.length) GameFrame.currentMenu =0;
                break;
            case KeyEvent.VK_ENTER:
                int currentMenu = GameFrame.currentMenu;
                if("开始游戏".equals(Constant.MENUS[currentMenu])){
                    toMap();
                }
                if("角色选择".equals(Constant.MENUS[currentMenu])){
                    toRoleSelect();
                }
                if("游戏帮助".equals(Constant.MENUS[currentMenu])){
                   toHelp();
                }
                if("游戏关于".equals(Constant.MENUS[currentMenu])){
                    toAbout();
                }
                if("退出游戏".equals(Constant.MENUS[currentMenu]))
                    GameFrame.gameState = Constant.GAMESTATE_OVER;
                if("选择存档".endsWith(Constant.MENUS[currentMenu])){
                    gameFrame.loadSave();
                    toSaveSelect();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                GameFrame.gameState = Constant.GAMESTATE_OVER;
                break;
        }
    }


    /**
     * 开始游戏的方法,将游戏状态更改为run
     */
    private void newGame(){
        gameFrame.resetGame();
        gameFrame.newGame();
    }

    /**
     * 选择角色时的键盘按键控制
     * @param keyCode
     */
    private void keyEventPressedRole(int keyCode) {

        switch (keyCode){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
                if(--GameFrame.currentRole<0) GameFrame.currentRole =GameFrame.roleNum-1;
                gameFrame.soundManager.playRoleSelectVoice(GameFrame.currentRole);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_A:
                if(++GameFrame.currentRole >= GameFrame.roleNum) GameFrame.currentRole =0;
                gameFrame.soundManager.playRoleSelectVoice(GameFrame.currentRole);
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_ESCAPE:
                toMenu();
                break;
        }
    }

    /**
     * 结束页面的按键事件
     * @param keyCode
     */
    private void keyEventPressedOver(int keyCode) {
        int length = Constant.CONFIRM.length;
        switch (keyCode){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
                if(--GameFrame.currentOver<0) GameFrame.currentOver =length-1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_A:
                if(++GameFrame.currentOver >= length) GameFrame.currentOver =0;
                break;
            case KeyEvent.VK_ENTER:
                if(GameFrame.currentOver == 0) System.exit(1);
                if(GameFrame.currentOver == 1) GameFrame.gameState = Constant.GAMESTATE_MENU;
                break;
            case KeyEvent.VK_ESCAPE:
                GameFrame.gameState = Constant.GAMESTATE_MENU;
                break;
        }
    }

    /**
     * 游戏运行的按键
     * @param keyCode
     */
    private void keyEventPressedRun(int keyCode) {

        //屏蔽胜利和失败状态的按键事件
        if(GameFrame.me.getState()==Constant.STATE_DIE||GameFrame.me.getState()==Constant.STATE_VEC)
            return;

        switch (keyCode){
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                GameFrame.me.setBoomDir(Constant.DIR_UP);
                GameFrame.me.setState(Constant.DIR_UP);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                GameFrame.me.setBoomDir(Constant.DIR_DOWN);
                GameFrame.me.setState(Constant.DIR_DOWN);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                GameFrame.me.setBoomDir(Constant.DIR_LEFT);
                GameFrame.me.setState(Constant.DIR_LEFT);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                GameFrame.me.setBoomDir(Constant.DIR_RIGHT);
                GameFrame.me.setState(Constant.DIR_RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                GameFrame.me.fire();
                break;
            case KeyEvent.VK_ESCAPE:
                GameFrame.gameState = Constant.GAMESTATE_RUN_pause;
        }
    }

    /**
     * 关于界面的按键
     * @param keyCode
     */
    private void keyEventPressedAbout(int keyCode) {
        toMenu();
    }


    /**
     * 松开按键的事件
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e){
        int keyCode = e.getKeyCode();
        switch(GameFrame.gameState){
            case Constant.GAMESTATE_MENU:
                break;
            case Constant.GAMESTATE_ROLE:
                break;
            case Constant.GAMESTATE_ABOUT:
                break;
            case Constant.GAMESTATE_RUN:
                keyEventReleasedRun(keyCode);
                break;
            case Constant.GAMESTATE_OVER:
                break;
        }
    }

    /**
     * 运行时的松开按键事件，主要控制移动和设计后的方向
     * @param keyCode
     */
    private void keyEventReleasedRun(int keyCode) {
        if(GameFrame.me.getState()==Constant.STATE_DIE||GameFrame.me.getState()==Constant.STATE_VEC)
            return;

        int state = GameFrame.me.getState();
        //向右
        if(state == Constant.STATE_ATTACK_L || state ==Constant.DIR_LEFT || state == Constant.DIR_DOWN)
            GameFrame.me.setState(Constant.STATE_STAND_L);
        else if(state == Constant.STATE_ATTACK_R || state ==Constant.DIR_RIGHT || state == Constant.DIR_UP )
            GameFrame.me.setState(Constant.STATE_STAND_R);
    }

    /**
     * 回到菜单的方法
     */
    private void toMenu(){
        GameFrame.gameState = Constant.GAMESTATE_MENU;
        gameFrame.soundManager.menuBgm();
    }

    /**
     * 进入存档选择页面的方法
     */
    private void toSaveSelect(){
        GameFrame.gameState = Constant.GAMESTATE_SAVE_SELECT;
        gameFrame.soundManager.saveBgm();
    }

    /**
     * 到角色选择页面的方法
     */
    private void toRoleSelect(){
        GameFrame.gameState = Constant.GAMESTATE_ROLE;
        gameFrame.soundManager.roleBgm();
    }

    /**
     * 到帮助页面的方法
     */
    private void toHelp(){
        GameFrame.gameState = Constant.GAMESTATE_HELP;
        gameFrame.soundManager.helpBgm();
    }

    /**
     * 到关于页面的方法
     */
    private void toAbout(){
        GameFrame.gameState = Constant.GAMESTATE_ABOUT;
        gameFrame.soundManager.aboutBgm();
    }

    /**
     * 到地图选择界面方法
     */
    private void toMap() {
        GameFrame.gameState = Constant.GAMESTATE_MAP;
        gameFrame.soundManager.mapBgm();
    }

}
