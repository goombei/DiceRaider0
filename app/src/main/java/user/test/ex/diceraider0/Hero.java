package user.test.ex.diceraider0;

import android.graphics.Rect;

/**
 * Created by INC-B-05 on 2016-04-05.
 */

/* Hogan
 * 2016-04-19
 * motion[] 삭제, motionBmp 삭제. 리소스를 Archive에서, 비트맵을 이제 외부에서 관리
 * frame -> motion으로 변경(View가 가진 비트맵 배열에서 찾을 index)
 *
 * 2016-04-05
 * Hero 생성자 및 이미지 반환 메서드 변경
 */

/* Hero
 * location     : 모험가의 위치 정보
 * motion       : 모험가의 이미지 아이디 배열( 정지, 이동 중 모두 포함 )
 * frame        : 모험가의 이미지를 불러오기 위한 배열 순번값
 *                정지[0]      이동 중[1, 2, 3]
 *
 * IS_MOVING    : 이동 정보 ( true : 이동 중, false : 정지 )
 *
 * getLocation()
 *      모험가 위치 정보 반환
 *
 * setLocation()
 *      모험가 위치 정보 저장
 *
 * setIsMoving()
 *      이동 정보 저장
 *
 * getMotion()
 *      이동 정보에 따라 motion[frame] 반환
 */

public class Hero implements Archive{

    Rect location;
    int event;

    private int motion = 0;
    boolean IS_MOVING;


    public Hero(Rect location) {
        this.location = location;

        motion = 0;
    }


    public Rect getLocation() {
        return location;
    }

    public void setIsMoving(boolean isMoving) {
        IS_MOVING = isMoving;
    }

    public int getMotion() {
        if (IS_MOVING){
            motion++;
            motion %= HERO_RES.length;

            return motion;
        } else {
            return 0;
        }
    }

    public void setLocation(Rect location) {
        this.location = location;
    }

    public int getEvent() {

        return event;
    }

    public void setEvent(int event) {

        this.event = event;
    }
}
