package user.test.ex.diceraider0;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by INC-B-13 on 2016-04-11.
 */
public class FireBall extends ImageView {

    private float moveX , moveY; // 이동 위치
    private int width , height;
    int damage; // 화살의 데미지
    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage *10;
    }



    public FireBall(Context context, int x, int y, int width, int height) {
        super(context);

        this.width = width;
        this.height = height;

        //this.img = imageView;

        setPositionX(x);
        setPositionY(y);
        moveX = 0;
        moveY = 0;
    }



    public boolean isOut()
    {

        if ( (getPositionX() > width) || (getPositionX() < 0) ||
                (getPositionY() > height) || (getPositionY() < 0) )
        {
            // 배경화면을 벗어나면 아웃.
            return true;
        }
        else return false;
    }

    public void move()
    {
        super.setX(super.getX() + moveX);
        super.setY(super.getY() + moveY);


    }

    public int getPositionX() {
        return (int)super.getX();
    }

    public void setPositionX(int positionX) {
        super.setX(positionX);
    }

    public int getPositionY() {
        return (int)super.getY();
    }

    public void setPositionY(int positionY) {
        super.setY(positionY);
    }

    public float getMoveX() {
        return moveX;
    }

    public void setMoveX(float moveX) {
        this.moveX = moveX;
    }

    public float getMoveY() {
        return moveY;
    }

    public void setMoveY(float moveY) {
        this.moveY = moveY;
    }

}
