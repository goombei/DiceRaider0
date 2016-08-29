package user.test.ex.diceraider0;

import android.graphics.Rect;

/**
 * Created by INC-B-05 on 2016-04-04.
 */

/* Hogan 2016-04-05
 * Tile 속성 및 이미지 그리기 방식 변경
 */

public class Tile implements Archive{
    //타일 정보

    //해당하는 타일 좌표값 저장 용도
    //좌표 등 타일 정보
    Rect location;

    //현재 타일 이벤트 설정 정보
    private int infoTile = 0;
    private int left;
    private int right;

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public Tile(Rect location, int infoTile) {
        this.location = location;

        left = location.left;
        right = location.right;

        this.infoTile = infoTile;
    }

    public void move(int step){
        left -= step;
        right -= step;
        location.set(left, location.top, right, location.bottom);

        // Log.i("MY", "" + location.left + " " + location.right + " " + (location.right - location.left));
    }

    public int getInfoTile() {
        return infoTile;
    }


    /*
    private int background;
    private int color;

    public Tile(Rect rect, int infoTile) {
        this.infoTile = infoTile;

        switch (this.infoTile) {
            case PLAIN:
                color = Color.GREEN;
                break;

            case TREASURE:
                color = Color.YELLOW;
                break;

            case TRAP:
                color = Color.BLUE;
                break;

            case SHOP:
                color = Color.MAGENTA;
                break;

            case ANTAGONIST:
                color = Color.BLACK;
                break;

            case BOSS:
                color = Color.RED;
                break;
        }
        // color = new Random().nextInt();
        color = color | 0xff000000;

        location = rect;
    }
    */
}
