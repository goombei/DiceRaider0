package user.test.ex.diceraider0;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.Random;

/**
 * Created by INC-B-13 on 2016-04-19.
 */
public class LockKeyView extends View implements Archive {

    
    int width , height;

    float[] pointX ,pointY;
    float deltaX , deltaY;
    float[] areaX , areaY;

    float preKeyY;
    boolean moveOk;
    boolean changeKeyBack = true;
    boolean[] backLimitFlag;
    int[] chgKeyBackNum;
    int inKey = 0;
    int bWidth ,bHeight, kWidth,kHeight;



    Context context;
    BitmapCentral b;
    Paint paint;

    public LockKeyView(Context context) {
        super(context);
        this.context = context;


        b = new BitmapCentral(context.getApplicationContext() , BITMAP_LockKey );


        /*//디바이스의 사이즈를 얻어낸다.
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //화면의 일반적인 정보를 담을 수 있도록 만들어진 클래스
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        */
        width = b.wWidth;
        height = b.wHeight;
        //bitmap = new Bitmap[LOCK_BITMAP_MAX];



        init();
        keyMove.sendEmptyMessageDelayed(0 , 10);
    }


    public void setDelta(float deltaX , float deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }


    // 변수 생성 및 위치 초기화 작업
    public void init()
    {
        areaX = new float[6];
        areaY = new float[6];
        pointX = new float[6];
        pointY = new float[6];
        chgKeyBackNum = new int[2];
        backLimitFlag = new boolean[5];
        paint = new Paint();
        b.createLockPicBmp(); // bmp 생성
        bWidth = b.lockpic[LOCK_BITMAP_PICK_B].getWidth();
        bHeight = b.lockpic[LOCK_BITMAP_PICK_B].getHeight();

        kWidth = b.lockpic[LOCK_BITMAP_KEY].getWidth();
        kHeight = b.lockpic[LOCK_BITMAP_KEY].getHeight();
        for ( int i = 0 ; i < LOCK_MAX_KEY;i++)
        {
            /*pointX[i] = width*0.95f/3 + (width*1.1f/14)*i ;
            pointY[i] = height/2;*/
            areaX[i] = width*2/5 - bWidth/3;
            pointX[i] =areaX[i] + bWidth/10;
            pointY[i] = height/1.98f + -bHeight*2/3 + bHeight*1.65f*(i-2);
            areaY[i] = pointY[i];
        }
        pointX[LOCK_STICK] = 0;
        pointY[LOCK_STICK] = height/2 + bHeight;

    }

    public void clearMap ()
    {
        for ( int i = 1 ; i < b.lockpic.length;i++)
        b.lockpic[i].recycle();
        b.lockpic = null;
        b = null;

    }

    public void setKey(int x , int y)
    {
        pointX[LOCK_STICK] = x;
        pointY[LOCK_STICK] = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 캔버스 그리기
        //inKey = 5;
        if ( inKey >= 5 )
        {
            // 종료 엑티비티
            ((KeyActivity)context).showDialogEnd();
            keyMove.removeMessages(0);
            return;
        }
        paint.setColor(Color.BLACK);
        // 배경화면 1
        canvas.drawRect(0, 0, width, height, paint);
        for ( int i = 0 ; i < LOCK_MAX_KEY ; i++)
        {
            if (pointX[i] > areaX[i] + bWidth / 2 )
            {
                // 키5개 영역 안쪽이면 골드
                canvas.drawBitmap(b.lockpic[LOCK_BITMAP_PICK_G], pointX[i], pointY[i], null);
                inKey++;
            }
            else
            {
                // 키5개 영역 안쪽이면 블랙
                canvas.drawBitmap(b.lockpic[LOCK_BITMAP_PICK_B], pointX[i], pointY[i], null);
            }
        }
        // 배경화면 2
        canvas.drawBitmap(b.lockpic[LOCK_BITMAP_BACK2], 0, 0, null);
        // 배경화면 3
        canvas.drawBitmap(b.lockpic[LOCK_BITMAP_BACK3], 0, 0, null);
        // 조종 스틱
        canvas.drawBitmap(b.lockpic[LOCK_BITMAP_KEY], pointX[LOCK_STICK], pointY[LOCK_STICK], null);

        inKey = 0;

        // 종료
    }




    public void checkLockPick(int n)
    {

        float keyStartX = pointX[LOCK_STICK] + kWidth;
        float keyStartY = pointY[LOCK_STICK];
        float keyEnd = pointY[n]+ bHeight*3/4;

        // 키 하나를 밀면 다른 키가 움직이는 구조로 설정
        if ((keyStartX > pointX[n] ) && (keyStartY > pointY[n]- bHeight/10 ) && (keyStartY < keyEnd  ))
        {
            // 스틱이 위 아래 이동할시  키 위로 넘어가지 않도록 변경
            if ((preKeyY < pointY[n]- bHeight/10 ))
            {
                pointY[LOCK_STICK] = preKeyY ;
                preKeyY =pointY[LOCK_STICK];
                return;
            }

            if ((preKeyY > pointY[n]+  bHeight*3/4 ))
            {
                pointY[LOCK_STICK] =preKeyY ;
                preKeyY =pointY[LOCK_STICK];
                return;
            }
            pointX[n] =keyStartX;

            if (changeKeyBack)
            {
                getKeyBackNum(n);
                changeKeyBack = false;
            }
                pointX[chgKeyBackNum[0]] -=  deltaX/2.5;
                pointX[chgKeyBackNum[1]] -=  deltaX/2.5;


            // 키가 나올수 있는 최대 범위
            for (int i = 0 ; i < LOCK_MAX_KEY;i++)
            {
                if (pointX[i] < areaX[0])
                {
                    pointX[i] = areaX[0];
                    backLimitFlag[i] = true;
                    //다른키가 움직이는것이 최대 범위로 바뀌면
                    if ( (i == chgKeyBackNum[0] ) || (i == chgKeyBackNum[1] ) )
                    {
                        // 움직이는 키를 조정한다.
                        changeKeyBack = true;
                    }
                }
                else
                {
                    backLimitFlag[i] = false;
                }
            }
        }
    }
    // 랜덤으로 버튼이 튀어나올 것을 정한다.
    void getKeyBackNum(int n)
    {
        Random rd = new Random();

        do {
            chgKeyBackNum[0] = rd.nextInt(5);
            chgKeyBackNum[1] = rd.nextInt(5);
        } while (chgKeyBackNum[0] == n || chgKeyBackNum[1] == n  ||
                 backLimitFlag[chgKeyBackNum[1]] == true ||backLimitFlag[chgKeyBackNum[0]] == true  );
    }

    // 노란색 안쪽의 사각인지 체크하는 매서드
    public boolean check_key_area( int n)
    {
        // 검사할 좌표를 잡는다.
        float areaStartX = areaX[n] + bWidth/2;
        float areastartY = areaY[n];
        float areaEndX = areaX[n] +bWidth*1.5f;
        float areaEndY = areaY[n]+bHeight;
        float keyStartX = pointX[LOCK_STICK] + kWidth;
        float keyStartY = pointY[LOCK_STICK];
        float keyEndY = pointY[LOCK_STICK] + kHeight/20;

        if ((keyStartX > areaStartX ) && (keyStartX < areaEndX ) &&
            (keyStartY > areastartY ) && (keyEndY < areaEndY ) )
        {
            return  true;

        }
        return  false;
    }

    public void checkInRect()
    {
        if (pointX[LOCK_STICK] +kWidth > areaX[0]+ bWidth / 2)
        {
            for ( int i = 0 ; i < LOCK_MAX_KEY ; i++ )
            {
                // 사각지역 안쪽으로 좌표가 들어가있는지 판단한다.
                moveOk = check_key_area(i);
                if (moveOk) break;
            }
            // 사각지역이 아니면 사각 노란색 화면 안쪽으로 못들어가게 한다.
            if (!moveOk)
                pointX[LOCK_STICK] = areaX[0] + bWidth/2-kWidth;

            if (pointX[LOCK_STICK] > areaX[0] + bWidth/2)
            {
                pointX[LOCK_STICK] = areaX[0] + bWidth/2;
            }
        }
    }

    public void checkOtherKey()
    {
        // 스틱이 다른키보다 안쪽으로 갈수 없다.
        for ( int i = 0 ; i < LOCK_MAX_KEY;i++)
        {
            if ((pointX[LOCK_STICK] +kWidth/4) > pointX[i] )
            {
                if (pointY[LOCK_STICK] > pointY[i] ) continue;
                pointX[LOCK_STICK] = pointX[i] - kWidth/4;
            }
        }
    }

    Handler keyMove = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            // 스틱의 X좌표 이동 최대 범위 설정
            if (pointX[LOCK_STICK] < 0 )
                pointX[LOCK_STICK] = 0;
            else if(pointX[LOCK_STICK] + kWidth > width)
            {
                pointX[LOCK_STICK] = width - kWidth;
            }
            else
            {
                // 내부에 있으면 이동 가능
                pointX[LOCK_STICK] += deltaX;
                // 키를 조정하다 다른것을 움직이려 하면 튀어나올 키를 바꾼다.
                if ( deltaX < 0 )
                {
                    changeKeyBack = true;
                }
                // 스틱을 조정하며 움직임을 결정
                checkInRect();
                checkOtherKey();
            }
            // 스틱의 Y좌표 이동 범위 결정
            if (pointY[LOCK_STICK] < 0 )
                pointY[LOCK_STICK] = 5;
            else if(pointY[LOCK_STICK] + kHeight > height)
            {
                pointY[LOCK_STICK] = height - kHeight;
            }
            else
            {
                // 사각 안에 있는지 판단 하여 Y 좌표를 못움직이게 함
                for ( int i = 0 ; i < LOCK_MAX_KEY ; i++ )
                {
                    moveOk = check_key_area(i);
                    if (moveOk) break;
                }
                if (!moveOk)
                {
                    preKeyY =pointY[LOCK_STICK];
                    pointY[LOCK_STICK] += deltaY;
                }
                    moveOk = false;
            }

            for ( int i = 0 ; i < LOCK_MAX_KEY ; i++)
            {
                checkLockPick(i);
            }
            invalidate();
            keyMove.sendEmptyMessageDelayed(0 , 10);
        }
    };


}
