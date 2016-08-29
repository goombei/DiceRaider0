package user.test.ex.diceraider0;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by INC-B-05 on 2016-04-07.
 */
/*
보스: 촉수괴물

조건: 촉수괴물을 공격하기 위해서는 마체테나 칼 등 날이 달린 아이템을 갖고 있어야 한다.(장착할 필요는 없음)

┌─t──t─┐
t      t
│   @  │
t      t
└─t──t─┘

화면을 둘러싼 촉수 중 각 방향에서 지정한 2곳 씩 총 8곳의 지점 중에서
촉수가 무작위로 등장하여 다른 촉수 쪽으로 뻗는다.
자신이나 같은 방향의 촉수로는 사라지지 않는다.
(목표값의 배열 순번을 2로 나눠 몫이 같으면 같은 방향이다.)
등장할 촉수는 공격하기 전에 그 위치에서 살짝 움직인다.
조짐 이후에 빠르게 등장하거나 느리게 등장한다.

플레이어 캐릭터가 촉수가 이동하는 위치에 있으면 데미지를 입는다.
조짐이 보이면 옮겨둬야 한다.

촉수가 너무 짧지 않은 시간 동안 나와 있다.
촉수가 나와있는 시간 동안 촉수의 가운데부터 시작점과 가까운 지점을 터치하면 촉수가 잘리며 데미지를 입는다.

 */

public class BossLounge extends View implements Archive{

    private Context context;
    private Paint paint;
    private BitmapCentral b;

    private BossVine boss; // 보스 동작 객체

    // 테스트용
    private boolean isTest = false; // 테스트 함수 동작
    private Rect rectBtn;

    // 화면 정보 및 기타 길이
    private int wWidth, wHeight;
    private int boardX, boardY, padding;
    private int bWidth;

    // 보스 구역 테두리
    private Rect board_frame;

    // 보스 개체 위치 배열 지정값
    private int tX = 0, tY = 1;

    // 보스 데미지 체크
    private boolean knife;
    private Rect weakPoint;

    // 덩굴 이미지 순번
    private int vine_frame = 0;

    // 동작
    // 현재 초
    private int currentSecond = 0;
    private int second = 0;
    private int frame = 0;
    private boolean isUp;

    // 바닥
    private BossTile[][] tile;
    private final int ROW = 6, COL = 6;
    boolean reset;

    //영웅
    private int heroX, heroY;
    int heroMaxHP = 0;
    private int heroHP = 0;
    private boolean diceChance;
    private boolean heroAttacked;
    private boolean heroInvulnerable;
    private Rect atkRectX, atkRectY;

    //게임 진행 중
    private boolean gameover;
    final int WORKING = -1;
    final int WIN_HERO = 1;
    final int WIN_BOSS = 2;


    //주사위
    private Dice dice;
    private int num_dice = 1;
    private int diceResult;

    public interface BossListener {
        void endLounge(Bundle rewards);
    }



    public BossLounge(Context context, Bundle bundle) {
        super(context);
        this.context = context;

        this.knife = bundle.getBoolean("knife");
        this.heroMaxHP = bundle.getInt("maxhp", 1000);
        this.heroHP = bundle.getInt("hp", heroMaxHP);


        paint = new Paint();
        b = new BitmapCentral(context, "BossLounge");

        wWidth = b.wWidth;
        wHeight = b.wHeight;

        boardX = 0;
        boardY = (wHeight - wWidth) / 2;

        padding = wWidth / 20;
        int divTile = wWidth / 80;
        bWidth = wWidth - 2 * padding;

        board_frame = new Rect(boardX, boardY, boardX + wWidth, boardY + wWidth); // 전투 장소

        // 주사위 객체 생성
        dice = new Dice();

        //칸 설정
        int tWidth = (wWidth - 2 * padding - divTile * (COL - 1)) / COL;
        int tHeight = (wWidth - 2 * padding - divTile * (ROW - 1)) / ROW;
        int tLeft, tTop;
        tile = new BossTile[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {

                tLeft = j * (tWidth + divTile) + padding;
                tTop = i * (tWidth + divTile) + boardY + padding;
                Rect tempRect = new Rect();
                tempRect.set(tLeft, tTop, tLeft + tWidth, tTop + tHeight);

                tile[i][j] = new BossTile(tempRect);
            }
        }

        // 보스 객체 생성
        boss = new BossVine(padding / 2, boardY + padding / 2, wWidth - padding, FPS);
        weakPoint = new Rect();

        //영웅 정보
        heroX = COL / 2;
        heroY = ROW / 2;
        heroInvulnerable = true;

        //비트맵 로드
        b.createDiceBmp(padding * 3, padding * 3);
        b.createHeroBmp(tWidth, tHeight);
        b.createVineBmp(padding * 3, wHeight);
        b.createWeakBmp(padding, padding);


        // 영웅 공격 범위
        atkRectX = new Rect();
        atkRectY = new Rect();
        atkRangeSet();


        //보스 동작 시작(타이머 시작)
        encounter();

        rectBtn = new Rect(0, board_frame.bottom, wWidth, wHeight);

        //테스트용
        if (isTest){
            test();
        }
    }// constructor

    private void test() {
        /*
        gameover = true;
        heroHP = 0;
        */
    } // test()

    private void encounter() {
        tile[heroY][heroX].isStart();
        handler.post(bossAction);
        handler.post(secondTimer);
    } // encounter()


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Log.i("MY", "draw");

        drawBoard(canvas);

        drawUnit(canvas, b.heroBmp[0], tile[heroY][heroX].location);

        if (boss.isSet()) {
            drawTentacle(canvas);
        }

        drawBackground(canvas);

        drawStatus(canvas);

        if (isTest) {
            drawTest(canvas);
        }

        drawDice(canvas);
    } // onDraw()


    private void drawStatus(Canvas canvas) {
        paint.setColor(Color.GREEN);
        canvas.drawRect(
                boardX + padding,
                boardY + wWidth + padding,
                (boardX + wWidth - padding) * heroHP / heroMaxHP,
                boardY + wWidth + padding * 2, paint);
    }

    private void drawDice (Canvas canvas) {
        canvas.drawBitmap(
                b.getDiceBmp(num_dice),
                (wWidth - b.getDiceBmp(num_dice).getHeight()) / 2,
                boardY + wWidth + padding,
                null );
    }

    private void drawBackground(Canvas canvas){
        paint.setColor(boss.getColor());
        canvas.drawRect(0, 0, wWidth, board_frame.top, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, board_frame.bottom, wWidth, wHeight, paint);
    } // drawBackground()


    private void drawUnit(Canvas canvas, Bitmap bitmap, Rect location) {
        int tempX, tempY;
        tempX = location.left + (location.width() - bitmap.getWidth()) / 2;
        tempY = location.top + (location.height() - bitmap.getHeight()) / 2;
        canvas.drawBitmap(bitmap, tempX, tempY, null);
    } // drawUnit()


    private void drawBoard(Canvas canvas) {
        paint.setColor(Color.WHITE);
        canvas.drawRect(board_frame, paint);
        for (int x = 0; x < wWidth; x += b.vineBmp[0].getWidth()){
            canvas.drawBitmap(b.vineBmp[0], x + padding, boardY, null);
            canvas.drawBitmap(b.vineBmp[1], x - padding, boardY - padding * 5 / 2, null);
        }


        for (BossTile[] t: tile){
            for (BossTile t1: t){
                paint.setColor(t1.color);
                canvas.drawRect(t1.location, paint);
            }
        }

    } // drawBoard()


    private void drawTest(Canvas canvas) {
        /*
        for (int i = 0; i< second; i++) {
            canvas.drawCircle( startX + (i * startX), startY - startX, 20, paint);
        }
        */
        paint.setColor(Color.LTGRAY);
        canvas.drawText("" + second, padding, padding, paint);
        canvas.drawText("" + frame, 2 * padding, padding, paint);
        canvas.drawText("" + boss.getHealth(), 3 * padding, padding, paint);
        canvas.drawText("" + boss.getWeakPoint(), 4 * padding, padding, paint);

        canvas.drawText("" + diceResult, padding, board_frame.bottom + padding, paint);
        canvas.drawText("" + diceChance, 2 * padding, board_frame.bottom + padding, paint);
        canvas.drawText("" + heroHP, 3 * padding, board_frame.bottom + padding, paint);

        drawDots(canvas);

        paint.setColor(Color.RED & 0x3fffffff);
        canvas.drawRect(atkRectX, paint);
        canvas.drawRect(atkRectY, paint);
    } // drawTest()

    private void drawDots(Canvas canvas) {
        for (int i = 0; i < boss.NUM_VINES; i++) {
            canvas.drawCircle(boss.getTentaclePosition(i, tX), boss.getTentaclePosition(i, tY), 5, paint);
        }
    }


    private void drawTentacle(Canvas canvas) {
        //좌표 및 방향 계산용
        float attX, attY, dirX, dirY, nxtX, nxtY, dx, dy;
        // float dstX, dstY;
        attX = boss.getTentaclePosition(boss.getAttacker(), tX);
        attY = boss.getTentaclePosition(boss.getAttacker(), tY);
        // dstX = boss.getTentaclePosition(boss.getDst(), tX);
        // dstY = boss.getTentaclePosition(boss.getDst(), tY);
        nxtX = boss.getTentaclePosition(boss.getNextAttacker(), tX);
        nxtY = boss.getTentaclePosition(boss.getNextAttacker(), tY);
        dirX = boss.getDirection(tX);
        dirY = boss.getDirection(tY);
        dx = dirX - attX;
        dy = dirY - attY;

        paint.setColor(boss.getColor());

        /*
        canvas.drawCircle(attX, attY, tentacleBmp.getWidth(), paint);
        */

        /*
        if (frame > 0) {
            paint.setStrokeWidth(padding);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawLine(attX - dx * padding, attY - dy * padding, dirX + dx, dirY + dy, paint);
        }
        */


        canvas.save();
        canvas.rotate(boss.rot(), dirX, dirY);

        float tempX = dirX - b.vineBmp[vine_frame].getWidth() / 2;
        float tempY = dirY - b.vineBmp[vine_frame].getHeight();
        canvas.drawBitmap(b.vineBmp[vine_frame], tempX, tempY, null);
        canvas.restore();


        //WeakPoint
        // canvas rotate 상태에서 rect를 지정해서 그려놓으면 이후에 터치 이벤트가 rotate에서 지정한 좌표로 적용가능한가 확인 필요
        if (!boss.isInvulnerable()){ //보스를 공격 가능할 때,
            float weakX = attX + dx / boss.PARTS * boss.getWeakPoint() * (3 / 2);
            float weakY = attY + dy / boss.PARTS * boss.getWeakPoint() * (3 / 2);
            Rect weakPoint = new Rect(
                    (int) weakX - padding / 2,
                    (int) weakY - padding / 2,
                    (int) weakX + padding / 2,
                    (int) weakY + padding / 2);

            this.weakPoint = null;
            this.weakPoint = weakPoint;

            if (weakPoint.intersect(atkRectX) || weakPoint.intersect(atkRectY)) {
                paint.setColor(Color.RED & 0x8fffffff);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                // canvas.drawRect(weakPoint, paint);
                canvas.drawCircle(weakX, weakY, padding / 2, paint);
                canvas.drawLine(0, weakY, wWidth, weakY, paint);
                canvas.drawLine( weakX, boardY, weakX, boardY + wWidth, paint);
                // canvas.drawBitmap(weakBmp, weakX - padding / 2, weakY - padding / 2, null);
            }
            paint.setStyle(Paint.Style.FILL);
        }

        //눈 그리기
        int leafDir = frame % 5;
        int leafMove
                = leafDir / 2 % 2 == 0? 1 : -1;

        nxtX -= padding * 3 / 4;
        nxtY -= padding * 3 / 8;
        canvas.drawBitmap(b.weakBmp, nxtX + frame * leafMove, nxtY * leafMove, null);

    } // drawTentacle()


    Handler handler = new Handler(); // timer second

    Runnable secondTimer = new Runnable() {
        @Override
        public void run() {
            if (!boss.isDefeated()) {
                postDelayed(this, 1000);
                currentSecond++;
                if(second <= 0){
                    second = boss.getWaitSec();
                }
                second--;
            } else {
                second = 0;
            }
        }
    };

    Runnable bossAction = new Runnable() {
        @Override
        public void run() {
            postDelayed(this, 1000 / FPS);

            int atk_range = padding;
            frame++;
            frame %= FPS;


            reset = boss.attack();

            if (reset) {
                heroInvulnerable = false;
                gameover = false;
                handler.post(spinDice);
            }

            if (!gameover) {
                heroInvulnerable = false;
            }

            // 모든 타일에 대해
            // 보스가 재공격을 시작하면 칸 상태 초기화
            // 보스가 공격 중 영웅이 있는 칸을 거쳤을 때 체크
            if (boss.isSpreading()){
                for (BossTile[] t : tile) {
                    for (BossTile t1 : t) {
                        if (reset) {
                            t1.reset();
                            diceChance = true;
                        }

                        boolean b = t1.checkDanger(
                                (int) boss.getDirection(tX),
                                (int) boss.getDirection(tY),
                                atk_range);

                        if (!heroAttacked) {
                            heroAttacked = b;
                        }

                    }
                }
            }

            //덩굴 애니메이션
            if (frame % 3 == 0) {
                vine_frame++;
                vine_frame %= b.vineBmp.length;
            }

            //보스가 칸을 거쳤으면
            if (!boss.isSpreading() && heroAttacked && !heroInvulnerable) {
                heroAttacked = false;
                heroHP -= DMG_BOSS_JUNGLE; // 체력 감소
            }

            //이기거나 졌으면
            if (heroHP <= 0 || boss.isDefeated()) {
                handler.removeCallbacksAndMessages(null);

                diceChance = false;
                heroInvulnerable = true;
                gameover = true;

                BossListener listener = (BossListener) context;
                Bundle bundle = boss.getRewards();
                bundle.putInt("hp", heroHP);

                listener.endLounge(bundle);
            }

            invalidate();
        }
    };

    Runnable spinDice = new Runnable() {
        @Override
        public void run() {
            postDelayed(this, SPIN_MSEC);

            num_dice%=6;
            num_dice++;
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //보스 공격을 확인하는 터치이벤트

                if (weakPoint != null) {
                    if(weakPoint.contains(x, y)) { // x, y가 보스의 약점을 클릭했는가
                        if (atkRectX.intersect(weakPoint) || atkRectY.intersect(weakPoint)){ // 공격 범위인가
                            boss.damage(knife);
                        }
                    }
                }

                if (rectBtn != null && diceChance) {
                    if (rectBtn.contains(x, y)) {
                        stopDice(x, y);
                    }
                }


                if (board_frame != null){
                    if (board_frame.contains(x, y)){
                        moveTile(x, y);
                    }
                }


                break;
        }

        return false;
    }

    private void stopDice(int x, int y) {
        removeCallbacks(spinDice);
        diceChance = false;
        diceResult = dice.roll(MAX_6);
        num_dice = diceResult;
        //diceResult = new Random().nextInt(6 + 1 - 1) + 1;

        for (int i = 0; i < tile.length; i++) {
            for (int j = 0; j < tile[i].length; j++) {
                int temp_x, temp_y;
                temp_x = j - heroX;
                temp_x = temp_x < 0 ? temp_x : -temp_x;
                temp_y = i - heroY;
                temp_y = temp_y < 0 ? temp_y : -temp_y;

                tile[i][j].checkDice( temp_x + temp_y + diceResult );
            }
        }
    }


    private void moveTile(int x, int y) {
        for (int i = 0; i < tile.length; i++) {
            for (int j = 0; j < tile[i].length; j++) {
                if ( tile[i][j].location.contains(x, y)
                        &&tile[i][j].isMovable() ){
                    // 옮길 수 있는 칸을 누를 시
                    // 기존 칸에서 영웅을 제거하고 새 칸에 영웅을 놓고 좌표 변경
                    tile[heroY][heroX].noHero();
                    tile[i][j].moveHero();
                    heroX = j;
                    heroY = i;
                    atkRangeSet();
                } else {
                    tile[i][j].resetMovable();
                }
            }
        }

        invalidate();
    }


    private void atkRangeSet(){
        atkRectX.set(
                tile[heroY][heroX].location.left - padding,
                tile[0][heroX].location.top - padding,
                tile[heroY][heroX].location.right + padding,
                tile[ROW - 1][heroX].location.bottom + padding);
        atkRectY.set(
                tile[heroY][0].location.left - padding,
                tile[heroY][heroX].location.top - padding,
                tile[heroY][COL - 1].location.right + padding,
                tile[heroY][heroX].location.bottom + padding);
    }


    public void remove() {
        handler.removeCallbacksAndMessages(null);
        b.remove();
    }

    /*
    public int getGameover() {
        if (gameover && boss.isDefeated()) {
            return WIN_HERO; // 승리
        } else if (gameover && heroHP <= 0) {
            boss.victory();
            return WIN_BOSS; // 패배
        } else {
            return WORKING; // 진행중
        }
    }
    */

}
