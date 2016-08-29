package user.test.ex.diceraider0;

import java.util.Random;

/**
 * Created by INC-B-05 on 2016-04-04.
 */
/* Hogan
 * 2016-04-19
 *
 */
public class Dice {

    //주사위 객체
    private final int MIN = 1;

    private int diceResult;

    private int num_rolled = 0;

    Random rnd = new Random();

    public int roll(int max) {
        diceResult = rnd.nextInt(max + MIN - 1) + MIN;
        num_rolled++;
        return diceResult;
    }

    public int getDiceResult() {
        return diceResult;
    }

    public int getNum_rolled() {
        return num_rolled;
    }

    public void reset() {
        num_rolled = 0;
    }
}
