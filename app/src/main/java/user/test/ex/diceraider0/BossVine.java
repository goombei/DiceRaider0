package user.test.ex.diceraider0;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;

import java.util.Random;

/**
 * Created by INC-B-05 on 2016-04-07.
 */
public class BossVine {

    //좌표 관련
    final int NUM_VINES = 8;
    private float[][] vinePosition = new float[NUM_VINES][2];

    // 각 방향의 좌표 left(0 1) top(2 3) right(4 5) bottom(6 7)
    private final int X = 0, Y = 1; // 2차원 배열에서 열값 0: x좌표, 1: y좌표
    private int tLength;
    private final int FULL_LENGTH;

    //보스 상태
    private int color = Color.BLACK; //보스 상태에 따른 색
    private boolean isDefeated;
    private int health; // 현재 체력
    final int FULLHP = 4; // 체력

    //공격 관련
    private int attacker = 0; // 공격하는 촉수 번호
    private int nextAttacker = 0; // 다음에 공격할 번호
    private int supporter = 0; // 공격 보조하는 촉수 번호
    int dst = 0; // 뻗을 방향
    private Path attackerPath;
    final int CHANCE = 3; // 빠른 공격 확률(100%에 대해 나누는 숫자)
    final int FASTATTACK = 1; // 빠른 다음 공격(초)
    final int SLOWATTACK = 2; // 느린 다음 공격(초)
    private int waitSec = 0;
    private boolean spreading;

    //피해 관련
    private int weakPoint = 0; // 약점
    private Rect weakPart;
    final int PARTS = 6;
    private boolean invulnerable;

    //보상 관련
    private String rewardItem = null; //보상 아이템
    private int rewardMoney = 100; //돈 보상

    //기타
    Random random = new Random();
    boolean isSet; // true: 세팅 끝 false: 세팅 중


    public BossVine(int startX, int startY, int bWidth, int fps) {
        this.health = FULLHP; //전체 체력 지정

        //공격자 초기값
        setAttacker();
        attackerPath = new Path();
        FULL_LENGTH = fps;
        tLength = 0;

        weakPart = new Rect();


        //화면 길이를 바탕으로 각 촉수에 대한 시작점 설정
        int tWidth = bWidth / 3;

        int h = NUM_VINES / 2; //촉수 반
        int q = NUM_VINES / 4; //촉수 1 / 4
        for (int i = 0; i < vinePosition.length; i++) {
            for (int j = 0; j < vinePosition[i].length; j++) {
                vinePosition[i][j]
                        = bWidth * (i / h) * ((i / q % q + (j + 1)) % q)
                        + tWidth * (( i / q % q + j ) % q) * ( i % q + 1 )
                        + startX * ( (j + 1) % 2 )
                        + startY * j;
            }
        }

        /*
        Log.i("MY", attacker + " "
                + vinePosition[nextAttacker][X] + " "
                + vinePosition[nextAttacker][Y]); */

        attackerPath.moveTo(vinePosition[nextAttacker][X], vinePosition[nextAttacker][Y]);
    }


    //보상아이템
    public String getRewardItem() {
        return rewardItem;
    }

    //보상금
    public int getRewardMoney() {
        return rewardMoney;
    }

    public Bundle getRewards() {
        Bundle rewards = new Bundle();
        int rewardMoney = isDefeated ?
                1000 : 0;

        String rewardItem = isDefeated ?
                "" : "";

        rewards.putInt("rewardMoney", rewardMoney);
        rewards.putString("rewardItem", rewardItem);
        rewards.putBoolean("StageClear", isDefeated);

        return rewards;
    }


    //setters & getters
    public int getColor() {
        return color;
    }

    public int getHealth() {
        return health;
    }

    public int getAttacker() {
        return attacker;
    }

    public int getNextAttacker() {
        return nextAttacker;
    }

    public int getDst() {
        return dst;
    }

    public int getWeakPoint() {
        return weakPoint;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public boolean isDefeated() {
        return isDefeated;
    }

    public boolean isSpreading() {
        return spreading;
    }

    public boolean isSet() {
        return isSet;
    }

    public int getWaitSec() {
        return waitSec;
    }

    public float getTentaclePosition(int num_tentacle, int xy) {
        // return vinePosition[num_tentacle][xy];
        // 길이에 따라 목표지점을 다르게 출력

        return vinePosition[num_tentacle][xy];
    }

    public float getDirection(int xy) {
        float tPos = vinePosition[dst][xy];

        if (tLength < FULL_LENGTH) {
            float delta = tPos - vinePosition[attacker][xy];
            tPos = vinePosition[attacker][xy] + delta / FULL_LENGTH * tLength;
        }

        return tPos;
    }


    public boolean attack() {
        if (!isDefeated){
            // 길이 변화
            if (spreading){
                tLength++;
            }

            // 공격 패스 반환
            if (waitSec == 0){
                isSet = false;
                setDst();


                // 다음 공격자 설정
                setAttacker();
                isSet = true;
                invulnerable = false;
                spreading = true;
                tLength = 0;

                return true;
            }
        }

        return false;
    }


    private void setAttacker() {
        //다음 공격자 설정
        nextAttacker = random.nextInt(NUM_VINES);
    }

    private void setDst() {
        //보조 공격자 설정
        supporter = attacker;
        for (int i = 0; i < NUM_VINES; i++) {
            if ( i / 2 == attacker / 2 && i != attacker ){
                supporter = i;
            }
        }

        //방향 설정
        do { //같은 방향의 촉수가 아니어야 한다.
            dst = random.nextInt(NUM_VINES);
        } while( dst == attacker || dst == supporter );

        //다음 공격 시간 설정
        int speed = random.nextInt(CHANCE);
        waitSec = speed % CHANCE == 0 ? FASTATTACK : SLOWATTACK;

        //약점 지정
        weakPoint = random.nextInt(PARTS);

        handler.postDelayed(timer, 1000);
    }


    public float rot(){
        float dx, dy; // x, y 에 대한 변화량(delta)

        dx = vinePosition[dst][X] - vinePosition[attacker][X];
        dx = dx == 0 ? 1 : dx;

        dy = vinePosition[dst][Y] - vinePosition[attacker][Y];
        dy = dy == 0 ? 1 : dy;

        double rot = Math.atan(dy / dx) * 180 / Math.PI;
        if (dx > 0) rot -= 180;

        return (float)rot + 90;
    }


    public boolean damage(boolean knife) {
        if (knife && !isDefeated && !invulnerable) {
            invulnerable = true;
            health--;

            color = health > FULLHP / 2 ? Color.BLACK //체력 반 이상시 검은색
                    : health > 1 ? Color.GREEN : Color.RED; // 반 이하 녹색, 임사상태 빨강

            if (health == 0) {
                defeated();
            }

            return true;
        }
        return false;
    }

    private void defeated(){
        //보스가 졌을 때 할 일
        isDefeated = true;
        handler.removeCallbacks(timer);
    }

    public void victory(){
        //보스가 이겼을 때 할 일
        //핸들러 종료
        handler.removeCallbacksAndMessages(null);
    }


    Handler handler = new Handler();

    Runnable timer = new Runnable() {
        @Override
        public void run() {
            if(!spreading){
                waitSec = 0;
                attacker = nextAttacker;
            } else {
                handler.postDelayed(this, 1000 * waitSec - 1);
                //1초 동안 몸을 편다
                spreading = false;
                tLength = FULL_LENGTH;
            }
        }
    };
}