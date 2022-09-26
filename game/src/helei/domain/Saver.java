package helei.domain;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * 存档类，封装存档的内容
 */
public class Saver implements Serializable {
    private static final long serialVersionUID = 1L;

    private int meX;
    private int meY;
    private int meHp;
    private int meType;
    private int mapType;

    private List<Integer> enemyX;
    private List<Integer> enemyY;
    private List<Integer> enemyHp;
    private List<Integer> enemyType;

    private List<Integer> tileX;
    private List<Integer> tileY;
    private int mapX;
    private int mapY;

    private long saveTime;
    private int killNumber;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getKillNumber() {
        return killNumber;
    }

    public void setKillNumber(int killNumber) {
        this.killNumber = killNumber;
    }

    public List<Integer> getTileX() {
        return tileX;
    }

    public void setTileX(List<Integer> tileX) {
        this.tileX = tileX;
    }

    public List<Integer> getTileY() {
        return tileY;
    }

    public void setTileY(List<Integer> tileY) {
        this.tileY = tileY;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getMapX() {
        return mapX;
    }

    public void setMapX(int mapX) {
        this.mapX = mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public void setMapY(int mapY) {
        this.mapY = mapY;
    }


    public int getMeType() {
        return meType;
    }

    public void setMeType(int meType) {
        this.meType = meType;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public int getMeX() {
        return meX;
    }

    public void setMeX(int meX) {
        this.meX = meX;
    }

    public int getMeY() {
        return meY;
    }

    public void setMeY(int meY) {
        this.meY = meY;
    }

    public int getMeHp() {
        return meHp;
    }

    public void setMeHp(int meHp) {
        this.meHp = meHp;
    }

    public List<Integer> getEnemyX() {
        return enemyX;
    }

    public void setEnemyX(List<Integer> enemyX) {
        this.enemyX = enemyX;
    }

    public List<Integer> getEnemyY() {
        return enemyY;
    }

    public void setEnemyY(List<Integer> enemyY) {
        this.enemyY = enemyY;
    }

    public List<Integer> getEnemyHp() {
        return enemyHp;
    }

    public void setEnemyHp(List<Integer> enemyHp) {
        this.enemyHp = enemyHp;
    }

    public List<Integer> getEnemyType() {
        return enemyType;
    }

    public void setEnemyType(List<Integer> enemyType) {
        this.enemyType = enemyType;
    }

    @Override
    public String toString() {
        return "Saver{" +
                "meX=" + meX +
                ", meY=" + meY +
                ", meHp=" + meHp +
                ", meType=" + meType +
                ", mapType=" + mapType +
                ", enemyX=" + enemyX +
                ", enemyY=" + enemyY +
                ", enemyHp=" + enemyHp +
                ", enemyType=" + enemyType +
                ", tileX=" + tileX +
                ", tileY=" + tileY +
                ", mapX=" + mapX +
                ", mapY=" + mapY +
                ", saveTime=" + saveTime +
                '}';
    }
}
