package user.test.ex.diceraider0;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * Created by INC-B-13 on 2016-04-11.
 */
public class Arrow extends ImageView {

    private float moveX , moveY; // 이동 위치
    private int width , height;

    Bitmap bitmap;

    int rotate;

    int damage; // 화살의 데미지
    boolean isarrow = false;

    public boolean isarrow() {
        return isarrow;
    }

    public void setIsarrow(boolean isarrow) {
        this.isarrow = isarrow;
    }

    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage *3;
    }



    public Arrow(Context context , int x, int y, int width, int height ) {
        super(context);

        this.width = width;
        this.height = height;

        //this.img = imageView;

        setPositionX(x);
        setPositionY(y);
        moveX = 0;
        moveY = 0;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.crossbow);
    }


    public void setRotate (int rotate)
    {
      this.rotate = rotate;
    }


    public boolean isOut()
    {

        if ( (getPositionX() >= width) || (getPositionX() <= 0) ||
                (getPositionY() >= height) || (getPositionY() <= 0) )
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
