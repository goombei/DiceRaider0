package user.test.ex.diceraider0;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

/**
 * Created by INC-B-05 on 2016-04-04.
 */

/* Hogan
 * 2016-04-26까지 변경점
 * BitmapCentral에서 Bitmap 관리
 * WallLinker interface 로 ainActivity 제어(버튼 활성화, 이벤트 감지 등)
 * 재시작, 스테이지 교체 추가
 * 기타 등등
 *
 * 2016-04-19
 * 이제부터 Bitmap을 WallView에서 모두 관리
 * 스테이지 확인 변수(currStage), 총 생성할 스테이지 정보(STAGE_MAX)는 Archive에서
 * 각 스테이지에 따라 다르게 그리기 위해 배열화한 객체들
 * StageInfo(스테이지 정보), map(타일 검색용), wallBmp(배경), wallX(배경 좌표)
 *
 * 2016-04-18
 * 전체 칸 갯수(BOARD_LENGTH)를 Archive로 옮김
 * Archive 구현
 *
 * 2016-04-05
 * Hero 생성자 및 이미지 반환 메서드 변경
 * Tile 속성 및 이미지 그리기 방식 변경
 */



public class WallView extends View implements Archive{

    private HashMap<Integer, Tile>[] map; // 칸 누적
    private BitmapCentral b;
    private int currStage = 0; // 현재 스테이지 (actNumber at DB)

    // 그릴 판의 좌표
    Rect board;


    Hero hero; // 영웅 객체

    // private Context context;

    private int wWidth, wHeight;//화면 사이즈

    private int tWidth, tHeight;//바닥 칸 개별 사이즈

    private int roadLength, roadWidth, roadHeight;//바닥 타일로 만든 길 전체 길이

    private final int SIGHT = 6; // ScrollView에서 캐릭터 앞으로 볼 수 있는 최대 칸 수

    private final int START_Y, END_Y; // 화면 세로 위치

    private boolean isMoving; // true: 이동 중, false: 정지
    private int location_hero = -1; // 현재 칸

    private int stepPerTile = 10; // 칸당 걸음 수
    private int num_step = 0; // 남은 걸음 수
    private int stepSize = 0; // 보폭
    private int num_move = 0; // 주사위로 받는 이동할 거리

    //재시작 관련
    private boolean isRestart; // 자리 초기화 여부
    private int stepDir = 1;

    //벽 관련
    int[] wallX;

    public interface WallLinker {
        void afterMove();
    }

    WallLinker wallLinker;


    public WallView(Context context) {
        super(context);

        this.wallLinker = (WallLinker) context;

        b = new BitmapCentral(context, "WallView");

        //화면 정보 바탕으로 위치 값 및 길이 값 등 설정
        //화면 사이즈 얻어오기
        wWidth = b.wWidth; // 화면 가로
        wHeight = b.wHeight; // 화면 세로

        tWidth = wWidth / 3; // 타일 가로 길이 = 화면 가로 / 3
        tHeight = wHeight / 6; // 타일 세로 길이 = 화면 세로 / 6

        roadLength = SIGHT + 2; // ScrollView에 표시할 타일의 수( 가시거리 + 뒷 타일 + 현 위치 )
        roadWidth = tWidth * roadLength; // ScrollView에 표시할 가로 길이
        roadHeight = tHeight; //ScrollView에 표시할 세로 길이

        //한 걸음 = 타일 길이 / 타일당 걸음 수
        stepSize = tWidth / stepPerTile;

        START_Y = tHeight * 7 / 2; // 타일을 그릴 세로 시작 위치
        END_Y = START_Y + tWidth; // 타일을 그릴 세로 끝 위치

        //그릴 판의 좌표
        board = new Rect(0, START_Y, roadWidth, END_Y);

        //스테이지 정보 생성 및 타일 정보 설정
        stageGenerate();

        //영웅 생성 및 이미지 저장
        Rect rectHero = new Rect(tWidth, tHeight * 3, tWidth * 2, tHeight * 5);
        hero = new Hero(rectHero);

        loadBmp();

    }//constructor


    public void setCurrStage(int currStage) {
        this.currStage = currStage;

        location_hero -= BOARD_LENGTH;
    }

    public int getCurrStage() {
        return currStage;
    }

    public void setIsRestart(boolean isRestart) {
        this.isRestart = isRestart;
        setNum_move(location_hero);
        invalidate();
    }

    public boolean isMoving() {
        return isMoving;
    }

    public int getRoadWidth() {
        return roadWidth;
    }

    public int getLocation_hero() {
        return location_hero;
    }

    public int getNum_move() {
        // 이동하는 칸 수
        return num_move;
    }


    public void setNum_move(int num_move) {
        // 이동할 칸 수 설정
        if ( num_move + location_hero >= BOARD_LENGTH ){
            this.num_move = BOARD_LENGTH - location_hero - 1;
            if (this.num_move < 0){
                this.num_move = 0;
            }
            if (wallLinker != null && !isRestart){
                wallLinker.afterMove();
            }
        } else {
            this.num_move = num_move;
        }

        location_hero += num_move;
        if (location_hero > BOARD_LENGTH - 1) {
            location_hero = BOARD_LENGTH - 1;
        }

        if (isRestart) {
            this.num_move = num_move + 1;
            location_hero = -1;
            stepDir = -1;
        }

        num_step = this.num_move * stepPerTile;
    }


    public int getNewEvent(){
        if (location_hero < BOARD_LENGTH && location_hero >= 0) {
            return map[currStage].get(location_hero).getInfoTile();
        } else
            return INVALID;
    }


    private void stageGenerate() {
        //map 크기와 난이도에 따라 유동적 배치
        currStage = 0;
        StageInfo[] stageInfo = new StageInfo[STAGE_MAX];
        map = new HashMap[STAGE_MAX]; // 타일을 관리할 해쉬맵

        for (int numStage = 0; numStage < STAGE_MAX; numStage++) {
            stageInfo[numStage] = new StageInfo(numStage, BOARD_LENGTH);
            map[numStage] = new HashMap<>();
        }

        // PLAIN    TREASURE    TRAP    SHOP   ANTAGONIST  BOSS
        // 0        1           2       3       4           5

        //타일 생성 Tile.location
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < BOARD_LENGTH; j++) {
                Rect rect = new Rect(tWidth * (j + 2), // + 캐릭터 칸 + 캐릭터 뒤
                        START_Y,
                        tWidth * (j + 3),
                        END_Y);
                //left,top,right,bottom

                Tile t = new Tile(rect, /*stageInfo[i].TILEINFO[j]*/ stageInfo[i].getInfoTable(j));
                map[i].put(j, t);
            }
        }

    }//stageGenerate()


    private void loadBmp() {
        wallX = b.createWallBmp(roadWidth, currStage);

        b.createHeroBmp(
                hero.getLocation().width(),
                hero.getLocation().height());

        b.setTileSize(tWidth, tWidth);
        b.createTileBmp();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int x : wallX) {
            canvas.drawBitmap(b.wallBmp, x, 0, null);
        }
        /*
        for (int i = 0; i < wallX.length; i++){
            canvas.drawBitmap(b.wallBmp, wallX[i], 0, null);
        }
        */

        for (int i = 0; i < BOARD_LENGTH; i++) {
            if (map[currStage].get(i).location.intersect(board)){
                // paint.setColor(map[currStage].get(i).color); // colorChk();

                canvas.drawBitmap(
                        b.tileBmp[currStage][ map[currStage].get(i).getInfoTile() ],
                        map[currStage].get(i).getLeft(),
                        map[currStage].get(i).location.top, null);
            }
        }

        if (isRestart) {
            canvas.save();
            canvas.rotate(
                    -90,
                    hero.getLocation().left + hero.getLocation().width() / 2,
                    hero.getLocation().top + hero.getLocation().height() / 2);
        }

        hero.setIsMoving(isMoving);
        canvas.drawBitmap(b.heroBmp[hero.getMotion()], hero.getLocation().left, hero.getLocation().top, null);

        if (isRestart) {
            canvas.restore();
        }

    }//onDraw()

    public int move() {

        isMoving = true;
        if (num_move > 0)
        {
            handler.post(step);
        } else if (isRestart){
            Log.i("MY", "restart");
            init_move();
        }

        return num_move;
    }//moving()


    public void stageUp() {
        b.createWallBmp(roadWidth, currStage);

        isMoving = true;
        invalidate();
        isMoving = false;
    }


    static Handler handler = new Handler();

    Runnable step = new Runnable() {
        @Override
        public void run() {
            if (!isRestart) {
                postDelayed(this, 1000 / stepPerTile); // 걸음 당 시간
            } else {
                postDelayed(this, 1000 / (stepPerTile * BOARD_LENGTH * 2));
            }

            // 한 걸음 계산
            num_step--;

            int one_step = stepSize;
            if (num_step % stepPerTile == 0){
                one_step = tWidth - stepSize * (stepPerTile - 1);
            }
            one_step *= stepDir;

            // 이동 종료
            if (num_step <= 0) {
                init_move();
            }

            // 타일 좌표 변경
            for (int i = 0; i < BOARD_LENGTH; i++) {
                map[currStage].get(i).move(one_step);
            }

            refreshWall(one_step); // 벽면 좌표 변경

            invalidate(); // 화면 갱신
        }
    };


    private void init_move() {
        removeCallbacks(step);
        isMoving = false;
        isRestart = false;
        stepDir = 1;
        num_move = 0;
        if (wallLinker != null) {
            Log.i("MY", "num_step " + num_step);
            wallLinker.afterMove();
        }
    }


    private void refreshWall(int one_step) {
        for (int i = 0; i < wallX.length; i++) {
            wallX[i] -= one_step;
            if (wallX[i] <= wWidth * -3){
                int offset = wallX[i] % wWidth;
                wallX[i] = wWidth * (wallX.length - 3) + offset;
            }
            if ( wallX[i] >= wWidth * (wallX.length - 2)) {
                int offset = wallX[i] % wWidth;
                wallX[i] = wWidth * -2 + offset;
            }
        }
    }

    public void remove() {
        handler.removeCallbacksAndMessages(null);
        b.remove();
    }
}
