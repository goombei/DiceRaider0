package user.test.ex.diceraider0;

import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by INC-B-05 on 2016-04-15.
 */
public class BossTile {

    Rect location;
    int color;
    private boolean isHero;
    private boolean isAttacked;
    private boolean isMovable;

    final int DANGER = Color.RED & 0x3fffffff;
    final int SAFE = Color.DKGRAY & 0x00ffffff;
    final int HERO = Color.YELLOW & 0x3fffffff;
    final int RANGE_MOVE = Color.CYAN & 0x8fffffff;


    public BossTile(Rect location) {
        this.location = location;
        reset();
    }

    private void setColor() {
        color = SAFE;

        if (isMovable) {
            color = RANGE_MOVE;
        }

        if (isAttacked) {
            color = DANGER;
        }

    }

    public void isStart() {
        isHero = true;
        setColor();
    }


    public void reset() {
        resetAttacked();
        resetMovable();
    }

    public void resetAttacked() {
        isAttacked = false;
        setColor();
    }

    public void resetMovable() {
        isMovable = false;
        setColor();
    }

    public boolean isAttacked() {
        return isAttacked;
    }

    public void checkDanger0(int attX, int attY, int dstX, int dstY) {

        boolean alignX, alignY;
        alignX = attX < dstX;
        alignY = attY < dstY;

        int tempLeft, tempRight, tempTop, tempBottom;
        tempLeft = alignX? attX : dstX;
        tempRight = alignX? dstX : attX;
        tempTop = alignY? attY : dstY;
        tempBottom = alignY? dstY : attY;

        Rect tempRect = new Rect(tempLeft, tempTop, tempRight, tempBottom);

        if (tempRect.intersect(location)) {
            isAttacked = true;
            setColor();
        }
    }

    public boolean checkDanger(int dstX, int dstY, int attackWidth){
        int range = attackWidth / 2;
        Rect rangeRect = new Rect(dstX - range, dstY - range, dstX + range, dstY + range);

        if (rangeRect.intersect(location)){
            isAttacked = true;
            setColor();
        }

        //영웅 공격받았는지
        return (isHero && isAttacked);
    }

    public void checkDice(int moveInfo){
        if(moveInfo > 0 && !isAttacked){
            isMovable = true;
            setColor();
        }
    }

    public boolean isMovable() {
        return isMovable;
    }

    public void noHero() {
        isHero = false;
        setColor();
    }

    public void moveHero(){
        if (isMovable){
            isHero = true;
            isMovable = false;
        }
        setColor();
    }
}