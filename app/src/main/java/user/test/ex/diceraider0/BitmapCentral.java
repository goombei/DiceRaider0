package user.test.ex.diceraider0;

/* Hogan
 * 2016-04-19
 * 비트맵을 생성하고 관리할 배열
 * 메소드와 인자를 통해 각 뷰에 맞는 만큼의 비트맵만 생성한다
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by INC-B-05 on 2016-04-19.
 */
public class BitmapCentral implements Archive{

    WeakReference<Context> ref;
    ArrayList<Bitmap> arrl_bmp;

    // Archive에 리소스 ID 저장해놓음
    Bitmap[][] tileBmp; // 타일
    Bitmap[] heroBmp; // 영웅
    // Bitmap[] wallBmp; // 벽
    Bitmap wallBmp;

    Bitmap[] diceBmp; // 주사위

    Bitmap[] vineBmp; // 덩굴
    Bitmap weakBmp; // 약점
    // 공격 예고지점
    Bitmap eyeBmp;

    Bitmap[]lockpic; // 록픽

    final int wWidth;
    final int wHeight;
    private int tWidth;
    private int tHeight;


    BitmapCentral(Context context, String mode) {
        ref = new WeakReference<>(context);
        arrl_bmp = new ArrayList<>();

        //화면 정보 바탕으로 위치 값 및 길이 값 등 설정
        //화면 사이즈 얻어오기
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        wWidth = dm.widthPixels; // 화면 가로
        wHeight = dm.heightPixels; // 화면 세로

        switch (mode) {
            case "WallView":
                break;

            case "EventDialog":
                tWidth = wWidth / 3;
                break;

            case "BossLounge":
                break;

            case "LockKey" :
                break;
        }


    }

    public void setTileSize(int tWidth, int tHeight) {
        this.tWidth = tWidth;
        this.tHeight = tHeight;
    }

    public void createSizes() {

    }


    private Bitmap createBmp(int res, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeResource(ref.get().getResources(), res);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true );
        arrl_bmp.add(bitmap);
        return bitmap;
    }

    private Bitmap createBmpKeepRatio(int res, int width, int height) {
        float ratio;
        int fixedW, fixedH;

        Bitmap bitmap = BitmapFactory.decodeResource(ref.get().getResources(), res);
        ratio = (float)bitmap.getHeight() / (float)bitmap.getWidth();
        if (ratio >= 1) {
            fixedW = (int)(width / ratio);
            fixedH = height;
        } else {
            fixedW = width;
            fixedH = (int)(height * ratio);
        }
        bitmap = Bitmap.createScaledBitmap( bitmap, fixedW, fixedH, true );

        arrl_bmp.add(bitmap);
        return bitmap;
    }


    public void createDiceBmp() {
        createDiceBmp(tWidth, tWidth);
    }

    public void createDiceBmp(int width, int height) {
        diceBmp = new Bitmap[DICE_RES.length];

        for (int i = 0; i < DICE_RES.length; i++) {
            diceBmp[i] = createBmp(DICE_RES[i], width, height);
        }
    }

    public void createTileBmp() {
        tileBmp = new Bitmap[STAGE_MAX][ALL_TILE_TYPES];

        for (int i = 0; i < TILE_RES.length; i++) {
            for (int j = 0; j < TILE_RES[i].length; j++) {
                if (i > 0 && (j == SHOP || j == BOSS)) {
                    tileBmp[i][j] = tileBmp[i - 1][j];
                } else {
                    tileBmp[i][j] = createBmp(TILE_RES[i][j], tWidth, tWidth);
                }

                // 정방형(tWidth * tWidth)
            }
        }
    }

    /*public int[][] createWallBmp(int roadWidth) {
        int[][] wallX = new int[STAGE_MAX][];

        // 배경화면 생성
        wallBmp = new Bitmap[STAGE_MAX];
        for (int numStage = 0; numStage < STAGE_MAX; numStage++) {
            wallBmp[numStage] = createBmp(WALL_RES[numStage], wWidth, wHeight);

            // 좌표 생성
            int n = roadWidth / wallBmp[numStage].getWidth() + 4; // 6 wall2 front2 back2
            wallX[numStage] = new int[n];
            for (int i = 0; i < wallX[numStage].length; i++){
                wallX[numStage][i] = wWidth * i - wWidth * 2;
            }
        }

        return wallX;
    }*/

    public int[] createWallBmp(int roadWidth, int currStage) {
        // 배경화면 생성
        wallBmp = null;
        wallBmp = createBmp(WALL_RES[currStage], wWidth, wHeight);
        int n = roadWidth / wallBmp.getWidth() + 4; // 6 wall2 front2 back2
        int[] wallX = new int[n];
        for (int i = 0; i < wallX.length; i++){
            wallX[i] = wWidth * i - wWidth * 2;
        }

        return wallX;
    }

    public void createHeroBmp(int width, int height) {
        // 영웅 이미지 생성
        heroBmp = new Bitmap[HERO_RES.length];
        for (int i = 0; i < HERO_RES.length; i++) {
            heroBmp[i] = (width == height) ?
                    createBmpKeepRatio(HERO_RES[i], width, height) :
                    createBmp(HERO_RES[i], width, height);
        }
    }


    public void createVineBmp(int width, int height) {
        //덩굴
        vineBmp = new Bitmap[VINE_RES.length];
        for (int i = 0; i < VINE_RES.length; i++) {
            vineBmp[i] = createBmp(VINE_RES[i], width, height);
        }
    }

    public void createWeakBmp(int width, int height) {
        weakBmp = BitmapFactory.decodeResource(ref.get().getResources(), WEAK_RES);
        weakBmp = Bitmap.createScaledBitmap(weakBmp, width, width, true);
    }

    public void createEyeBmp(int width, int height) {
        // 출몰예정지
        eyeBmp = createBmp(EYE_RES, width, height);
    }

    // 록픽 관련 비트맵 설정
    public void createLockPicBmp()
    {
        lockpic = new Bitmap[LOCK_BITMAP_MAX];


        for ( int i = 1 ; i < LOCK_BITMAP_MAX;i++)
        {
            lockpic[i] = BitmapFactory.decodeResource(ref.get().getResources(),LOCKPIC_RES[i] );
        }


        for ( int i = 1 ; i < 3;i++)
        {
            lockpic[i] = Bitmap.createScaledBitmap(lockpic[i], wWidth, wHeight, true);
        }

        lockpic[LOCK_BITMAP_KEY] = Bitmap.createScaledBitmap(lockpic[LOCK_BITMAP_KEY],
                wWidth/10, wHeight/3, true);

        lockpic[LOCK_BITMAP_PICK_B] = Bitmap.createScaledBitmap(lockpic[LOCK_BITMAP_PICK_B],
                wWidth/5, wHeight/21, true);

        lockpic[LOCK_BITMAP_PICK_G] = Bitmap.createScaledBitmap(lockpic[LOCK_BITMAP_PICK_G],
                wWidth/5 , wHeight/21, true);
    }


    public Bitmap getDiceBmp(int i) {
        return diceBmp[i - 1];
    }

    public void remove() {
        while(arrl_bmp.size() != 0){
            clean( arrl_bmp.get(0) );
            arrl_bmp.remove(0);
        }
    }

    public void clean(Bitmap bitmap) {
        if (bitmap != null) bitmap = null;
    }


}
