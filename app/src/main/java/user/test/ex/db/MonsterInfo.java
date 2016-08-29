package user.test.ex.db;

import user.test.ex.diceraider0.R;

/**
 * Created by INC-B-17 on 2016-04-15.
 */
public class MonsterInfo {

    final int IMG_MON_NUM1 = R.anim.low_monster_bat;
    final int IMG_MON_NUM2 = R.anim.low_monster_bat +1;
    final int IMG_MON_NUM3 = R.anim.low_monster_bat +2;
    final int IMG_MON_NUM4 = R.anim.low_monster_bat +3;
    final int IMG_MON_NUM5 = R.anim.low_monster_bat +4;
    final int IMG_MON_NUM6 = R.anim.low_monster_bat +5;

    final int IMG_MON_NUM7 = R.anim.middle_monster_bat;
    final int IMG_MON_NUM8 = R.anim.middle_monster_bat +1;
    final int IMG_MON_NUM9 = R.anim.middle_monster_bat +2;
    final int IMG_MON_NUM10 = R.anim.middle_monster_bat +3;
    final int IMG_MON_NUM11 = R.anim.middle_monster_bat +4;
    final int IMG_MON_NUM12 = R.anim.middle_monster_bat +5;

    final int IMG_MON_NUM13 = R.anim.high_monster_bat;
    final int IMG_MON_NUM14 = R.anim.high_monster_bat +1;
    final int IMG_MON_NUM15 = R.anim.high_monster_bat +2;
    final int IMG_MON_NUM16 = R.anim.high_monster_bat +3;
    final int IMG_MON_NUM17 = R.anim.high_monster_bat +4;
    final int IMG_MON_NUM18 = R.anim.high_monster_bat +5;



    int[] lowMonsterImages = new int[] {IMG_MON_NUM1, IMG_MON_NUM2, IMG_MON_NUM3, IMG_MON_NUM4, IMG_MON_NUM5, IMG_MON_NUM6};
    int[] middleMonsterImages = new int[] {IMG_MON_NUM7, IMG_MON_NUM8, IMG_MON_NUM9, IMG_MON_NUM10, IMG_MON_NUM11, IMG_MON_NUM12};
    int[] highMonsterImages = new int[] {IMG_MON_NUM13, IMG_MON_NUM14, IMG_MON_NUM15, IMG_MON_NUM16, IMG_MON_NUM17, IMG_MON_NUM18};

    public int getLowMonsterImages(int n) {
        return lowMonsterImages[n];
    }

    public int getMiddleMonsterImages(int n) {
        return middleMonsterImages[n];
    }

    public int getHighMonsterImages(int n) {
        return highMonsterImages[n];
    }


    String[] LowLevelName = new String[]{"spider, bat, snake"};
    int act1LowLevelHp = 350;
    //실제 공격력 계산은 getter함수에서 하고 있음
    int act1LowLevelAtt = 0;
    int act1LowLevelGold = 180;

    String[] MiddleLevelName = new String[]{"soldier, crazy, native"};
    int act1MiddleLevelHp = 500;
    int act1MiddleLevelAtt = 0;
    int act1MiddleLevelGold = 600;

    int act1BoxGold = 180;

    int act2LowLevelHp = 400;
    int act2LowLevelAtt = 0;
    int act2LowLevelGold = 550;

    int act2MiddleLevelHp = 820;
    int act2MiddleLevelAtt = 0;
    int act2MiddleLevelGold = 1800;

    int act2BoxGold = 550;

    int act3LowLevelHp = 500;
    int act3LowLevelAtt = 0;
    int act3LowLevelGold = 1230;

    int act3MiddleLevelHp = 1020;
    int act3MiddleLevelAtt = 0;
    int act3MiddleLevelGold = 4000;

    int act3BoxGold = 1230;

    public String[] getLowLevelName() {
        return LowLevelName;
    }

    public int getAct1LowLevelHp() {
        return act1LowLevelHp;
    }

    public int getAct1LowLevelAtt(int myDice, int enemyDice) {
        int result = enemyDice - myDice;
        return (1/2 * (result * 100));
    }

    public int getAct1LowLevelGold() {
        return act1LowLevelGold;
    }

    public String[] getMiddleLevelName() {
        return MiddleLevelName;
    }

    public int getAct1MiddleLevelHp() {
        return act1MiddleLevelHp;
    }

    public int getAct1MiddleLevelAtt(int myDice, int enemyDice) {
        int result = enemyDice - myDice;
        return ((1+1) / 2 * (result * 100));
    }

    public int getAct1MiddleLevelGold() {
        return act1MiddleLevelGold;
    }

    public int getAct1BoxGold() {
        return act1BoxGold;
    }

    public int getAct2LowLevelHp() {
        return act2LowLevelHp;
    }

    public int getAct2LowLevelAtt(int myDice, int enemyDice) {
        int result = enemyDice - myDice;
        return ((1+2) / 2 * (result * 100));
    }

    public int getAct2LowLevelGold() {
        return act2LowLevelGold;
    }

    public int getAct2MiddleLevelHp() {
        return act2MiddleLevelHp;
    }

    public int getAct2MiddleLevelAtt(int myDice, int enemyDice) {
        int result = enemyDice - myDice;
        return ((2+2) / 2 * (result * 100));
    }

    public int getAct2MiddleLevelGold() {
        return act2MiddleLevelGold;
    }

    public int getAct2BoxGold() {
        return act2BoxGold;
    }

    public int getAct3LowLevelHp() {
        return act3LowLevelHp;
    }

    public int getAct3LowLevelAtt(int myDice, int enemyDice) {
        int result = enemyDice - myDice;
        return ((1+3) / 2 * (result * 100));
    }

    public int getAct3LowLevelGold() {
        return act3LowLevelGold;
    }


    public int getAct3MiddleLevelHp() {
        return act3MiddleLevelHp;
    }

    public int getAct3MiddleLevelAtt(int myDice, int enemyDice) {
        int result = enemyDice - myDice;
        return ((2+2) / 2 * (result * 100));
    }

    public int getAct3MiddleLevelGold() {
        return act3MiddleLevelGold;
    }

    public int getAct3BoxGold() {
        return act3BoxGold;
    }
}
