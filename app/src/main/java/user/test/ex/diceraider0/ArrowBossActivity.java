package user.test.ex.diceraider0;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import user.test.ex.db.DBUserInfo;

public class ArrowBossActivity extends AppCompatActivity implements  Archive {
    private FrameLayout touchLo; // 터치 판단할 레이아웃
    private float[] touchX , touchY; // 터치용 x , y 값
    private float deltaX , deltaY;   // 시작지점부터 이동한 위치까지의 가변값
    private float theta , theta2 = 0; // 1. 첫터치지점과 최종 이동한 지점까지의 선 각도 (degree , radian )
    private float shotPower; // 세기
    private float moveX , moveY; // 이동 위치
    int width , height; // 현재 뷰의 크기
    int crossBowW ,crossBowH;
    int arrowW;
    int heroAtt;
    int speedX = 5; // 보스 이동 속도
    ArrayList<Arrow> arrlist; // 화살 어레이 리스트
    ArrayList<FireBall> battacklist; // 보스 공격 어레이 리스트
    Bitmap[] bitmap; // 사용 이미지 비트맵 변환

    TextView bosshp , herohp;

    ImageView arrow_boss , heroimg , boss_wp , bossWarning; // 표시용 이미지 뷰

    Dialog dialog; // 보스 이벤트 결과
    Button finishBtn; // 다이얼로그 이벤트

    boolean isStart = false;
    boolean resultGame = false; // 승리 패배판단 false : 패배 , true = 승리
    boolean init = false;
    // 타임 딜레이를 주기 위한 함수
    long myBaseTime; // 현재시간
    int myDelayTime = 15000; // 턴 변화 시간
    long myBossAttTime;

    int bossMaxHp = 200; // 보스 최대 체력
    int bossnowHp = 200; // 보스 현재 체력
    int bossViewHp = 200; // 그래픽용 보스 체력

    int heroMaxHp = 100; // 유저 최대 체력
    int heroNowHp = 50; // 유저 현재 체력
    int heroViewHp = 100; // 그래픽용 유저 체력
    int gameMode = 0;     // 게임 공격 / 방어 모드 설정

    int heromoney;

    int bossAttackcnt = 0;

    int bossBackColor;
    int heroBackColor;

    // 보스 애니메이션용 비트맵 변환
    Bitmap[] anim;
    int animDelay;
    int animNum;

    //애니메이션
    Animation animation;

    //사운드
    SoundManager sm;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrow_boss_activity);

        touchX = new float[2]; // 터치 x좌표
        touchY = new float[2]; // 터치 y좌표

        touchLo = (FrameLayout)findViewById(R.id.touchLo);
        touchLo.setOnTouchListener(touch);
        bosshp = (TextView)findViewById(R.id.bosshp); // 보스 체력
        herohp = (TextView)findViewById(R.id.herohp); // 영웅 체력
        arrow_boss = (ImageView)findViewById(R.id.arrow_boss);// 보스 이미지
        heroimg = (ImageView)findViewById(R.id.heroimg);// 보스 이미지
        boss_wp = (ImageView)findViewById(R.id.boss_wp);// 보스 이미지
        bossWarning = (ImageView)findViewById(R.id.bossWarning); // 글자 이미지
        arrlist = new ArrayList<>();
        battacklist = new ArrayList<>();



        bitmap = new Bitmap[5];
        bitmap[BITMAP_CROSSBOW] = BitmapFactory.decodeResource(getResources(),ARBOSS_RES[0]);
        bitmap[BITMAP_CROSSBOW_AIM] = BitmapFactory.decodeResource(getResources(), ARBOSS_RES[1]);
        bitmap[BITMAP_ARROW] = BitmapFactory.decodeResource(getResources(), ARBOSS_RES[2]);

        bitmap[BITMAP_FIREBALL] = BitmapFactory.decodeResource(getResources(), ARBOSS_RES[3]);
        bitmap[BITMAP_DRAGONWP] =BitmapFactory.decodeResource(getResources(), ARBOSS_RES[10]);

        //디바이스의 사이즈를 얻어낸다.
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        //화면의 일반적인 정보를 담을 수 있도록 만들어진 클래스
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        // 활 사이즈 변환
        bitmap[BITMAP_CROSSBOW] = Bitmap.createScaledBitmap(bitmap[BITMAP_CROSSBOW] , width/5 , height/10 , false);
        bitmap[BITMAP_CROSSBOW_AIM] = Bitmap.createScaledBitmap(bitmap[BITMAP_CROSSBOW_AIM], width / 5, height / 10, false);
        // 화살 사이즈 변환
        bitmap[BITMAP_ARROW] = Bitmap.createScaledBitmap(bitmap[BITMAP_ARROW], width / 20, height / 15, false);

        bitmap[BITMAP_FIREBALL] = Bitmap.createScaledBitmap(bitmap[BITMAP_FIREBALL], width / 20, height / 10, false);
        bitmap[BITMAP_DRAGONWP] = Bitmap.createScaledBitmap(bitmap[BITMAP_DRAGONWP], width / 30, height / 40, false);
        myBaseTime = SystemClock.elapsedRealtime();

        /*handler.postDelayed(dmgHandler, 10);
        handler.postDelayed(imgMove, 10);
*/

        //dmgHandler.sendEmptyMessageDelayed(0, 10);
        heroimg.setImageBitmap(bitmap[BITMAP_CROSSBOW]);
        heroimg.setVisibility(View.INVISIBLE);
        gameMode = BOSS_NORMAL;
        isStart = true;



        anim = new Bitmap[6];

        for ( int i = 0 ; i < anim.length;i++)
        {
            anim[i] = BitmapFactory.decodeResource(getResources(), ARBOSS_RES[4+i]);
            anim[i] = Bitmap.createScaledBitmap(anim[i] , width/2 , height/4 , false);
        }
        animNum = 0;
        animDelay = 10;
        arrow_boss.setImageBitmap(anim[animNum]);
        boss_wp.setImageBitmap(bitmap[BITMAP_DRAGONWP]);



        heroMaxHp  = DBUserInfo.userInfo.getMaxHp();
        heroNowHp  = DBUserInfo.userInfo.getHpParseInt();
        heroViewHp = DBUserInfo.userInfo.getHpParseInt();
        heromoney = DBUserInfo.userInfo.getMoneyParseInt();
        heroAtt = DBUserInfo.userInfo.getActNumber();


        animation = AnimationUtils.loadAnimation(
                ArrowBossActivity.this, R.anim.menu_invisible);

        sm = new SoundManager(this);

        if (getIntent().getBooleanExtra("boss1_thrupass", false)) {
            handler.postDelayed(finishBoss, 3000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.playBGM(sm.BGM_SECONDBOSS, false);
        handler.postDelayed(imgMove , 10);
        handler.postDelayed(dmgHandler , 10);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.stopBGM();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (event.getAction()) {
            case KeyEvent.KEYCODE_BACK:
                break;
        }

        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        crossBowW = bitmap[BITMAP_CROSSBOW].getWidth();
        crossBowH = bitmap[BITMAP_CROSSBOW].getHeight();
        arrowW =bitmap[BITMAP_ARROW].getWidth();
        if(!init)
        {
            herohp.setLayoutParams(new LinearLayout.LayoutParams(width * heroViewHp / heroMaxHp, ViewGroup.LayoutParams.WRAP_CONTENT));
            init = true;
        }
    }



    View.OnTouchListener touch = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (gameMode == BOSS_ATTACK)
            {
                for ( int i = 0 ; i < arrlist.size();i++)
                {
                    Arrow ar = arrlist.get(i);
                    touchLo.removeView(ar);
                    arrlist.remove(ar);
                    i--;
                }
            }

            if  (motionEvent.getAction() == MotionEvent.ACTION_DOWN )
            {
                if (gameMode == BOSS_NORMAL)
                {
                    // 시작지점 설정
                    // 터치 시작 지점
                    touchX[0] = motionEvent.getX();
                    touchY[0] = motionEvent.getY();
                    if (touchY[0] <(touchLo.getHeight()*3/4)) return false;
                    setFirstXY((int) motionEvent.getX(), (int) motionEvent.getY());
                }
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE )
            {
                if (gameMode == BOSS_ATTACK)
                {
                    float setX;
                    if (motionEvent.getX()+crossBowW/2 > width)
                    {
                        setX = width-crossBowW;
                    }
                    else if (motionEvent.getX()-crossBowW/2 < 0)
                    {
                        setX = 0;
                    }
                    else
                    {
                        setX = motionEvent.getX()-crossBowW/2;
                    }

                    heroimg.setX(setX);

                }
                else
                {

                    if (touchY[0] <(touchLo.getHeight()*3/4)) return false;
                    if (arrlist.size() == 0) return false;
                    Arrow ar = arrlist.get(arrlist.size() - 1);
                    ar.setImageBitmap(bitmap[BITMAP_CROSSBOW_AIM]);
                    setNextXY((int) motionEvent.getX(), (int) motionEvent.getY());
                    // 디그리 각도 구하는 공식
                    theta = (float) Math.atan2(deltaX, deltaY) / (float) Math.PI * 180; // 디그리
                    // 라디안 구하는 공식
                    theta2 = (float) Math.atan(deltaY / deltaX); // 라디안
                    ar.setRotation(theta - 180);
                }
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP )
            {
                if (gameMode == BOSS_ATTACK) return false;
                else {
                    if (touchY[0] <(touchLo.getHeight()*3/4)) return false;
                    if (arrlist.size() == 0) return false;
                    setShotPower();
                    // 이미지 화살 모양으로 변경
                    Arrow ar = arrlist.get(arrlist.size() - 1);
                    // 화살이 중앙에 오도록 변경
                    ar.setImageBitmap(bitmap[BITMAP_ARROW]);
                    ar.setPositionX((int) touchX[0] - arrowW / 2);
                    ar.setPositionY((int) touchY[0] - crossBowH / 2);
                    ar.setDamage((int) shotPower / 5);
                    ar.setIsarrow(true);
                    // 이미지 이동 시작
                    ar.setMoveX(moveX);
                    ar.setMoveY(moveY);
                }
            }

            return true;
        }
    };

    void setShotPower ( )
    {
        // 대각선의 길이를 구하기 위해 삼각형의 빗변 구하는 공식을 적용 - 루트( x제곱 + y제곱)
        shotPower = (int)Math.sqrt((double)(deltaY*deltaY+deltaX*deltaX)) / 3; // 파워 조정
        shotPower += ( heroAtt / 10 ); // 히어로의 공격력을 추가함
        // 게임의 난이도를 위해 최대 및 최소 파워 설정
        if (shotPower >50) shotPower = 50;
        else if (shotPower <20) shotPower = 20;
        // 파워에 따른 x좌표 , y좌표 이동해야할 값 설정 ( 삼각형의 빗변을 기준으로 x값 ,y값 계산 )
        moveX = (float)(shotPower*Math.cos(theta2));
        moveY = (float)(shotPower*Math.sin(theta2));
        // 0도를 기준으로 x좌표 y좌표를 각각 반전해야 원하는 이동 경로가 나옴
        if (theta >= 0 )
        {
            moveX *= -1;
        }
        else
        {
            moveY *= -1;
        }
    }


    void setFirstXY (int x , int y)
    {
        // 이미지 생성 및 초기값 설정
        setImgXY(x, y);
    }

    void setNextXY ( int x , int y)
    {
        // 터치 이동 지점
        touchX[1] = x;
        touchY[1] = y;
        // 연산 처리
        deltaX = touchX[1] - touchX[0];
        deltaY = touchY[0] - touchY[1];

    }
    void setImgXY ( int x , int y)
    {
        Arrow ar = new Arrow(this, x, y, width , height);
        ar.setImageBitmap(bitmap[BITMAP_CROSSBOW]);
        // 파라메타 설정
        FrameLayout.LayoutParams params = new
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ar.setLayoutParams(params);
        // 터치한 좌표에 활 객체 생성
        ar.setPositionX(x - crossBowW / 2);
        ar.setPositionY(y - crossBowH / 2);
        // 시작지점에 마지막 방향값을 고정시킨다.


        // 리스트 뷰에 추가
        arrlist.add(ar);
        // 이미지 추가
        touchLo.addView(ar);


        //  터치 무브가 안될경우 디폴트값 지정
        theta = 180; // 값 초기화
        deltaX = 0;
        deltaY = -10;
        theta2 = (float) Math.atan(deltaY / deltaX); // 라디안
        ar.setRotation(theta - 180);
    }

    public void checkArrow()
    {
        for ( int i = 0; i < arrlist.size();i++)
        {
            Arrow ar = arrlist.get(i);
            ar.move();
            if (ar.isOut()) {
                // 화면을 나간 미사일을 ArrayList에서 제거
                arrlist.remove(ar);
                touchLo.removeView(ar);
            }
        }
    }

    public void checkBossAttack ()
    {

        for ( int i = 0; i < battacklist.size();i++)
        {
            FireBall f = battacklist.get(i);
            f.move();
            if (f.isOut())
            {
                // 화면을 나간 미사일을 ArrayList에서 제거
                battacklist.remove(f);
                touchLo.removeView(f);
            }
        }
    }

    // 보스 이동
    private void doboss()
    {
        arrow_boss.setX(arrow_boss.getX() + speedX);
        boss_wp.setX(arrow_boss.getX()+arrow_boss.getWidth()/2 - boss_wp.getWidth()/4);
        boss_wp.setY(arrow_boss.getY()+arrow_boss.getHeight()/2);
        if ((boss_wp.getX() <= 0) || (boss_wp.getX() >= width - boss_wp.getWidth()))
        {
            /*if (arrow_boss.getX() >= width - arrow_boss.getWidth())
            {
                arrow_boss.setX(width - arrow_boss.getWidth());
            }*/
            speedX *= -1; // 속도를 반대로 전환
        }
    }//doRabbit

    // 보스 시작위치에서 공격
    private void bossAttack (float x , float y)
    {
        float theta;
        float theta2;
        float moveX;
        float moveY;
        int power;
        float deltaX;
        float deltaY;
        float[][] target ={{0,0},{0,0}};
        bossAttackcnt++;
        // 시간에 따라 파이어 볼을 날림
        for ( int i = 0 ; i < 5 ; i++)
        {
            FireBall  f = new FireBall(ArrowBossActivity.this ,(int)x , (int)y , width , height );
            FrameLayout.LayoutParams params = new
                    FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            f.setLayoutParams(params);
            target[1][0] = x;
            target[1][1] = y;

            target[0][0] =heroimg.getX() + crossBowW/2 + (width/10)*(i-2);
            target[0][1] =heroimg.getY();

            deltaX = target[1][0] - target[0][0];
            deltaY = target[0][1] - target[1][1];

            theta = (float) Math.atan2(deltaX, deltaY) / (float) Math.PI * 180; // 디그리
            // 라디안 구하는 공식
            theta2 = (float) Math.atan(deltaY / deltaX); // 라디안


            // 대각선의 길이를 구하기 위해 삼각형의 빗변 구하는 공식을 적용 - 루트( x제곱 + y제곱)
            power = (int)Math.sqrt((double)(deltaY*deltaY+deltaX*deltaX)) / 3; // 파워 조정
            // 게임의 난이도를 위해 최대 및 최소 파워 설정
            if (power >30) power = 30;
            // 데미지 설정
            f.setDamage(power);

            power = ( power * height ) / 2560;

            // 파워에 따른 x좌표 , y좌표 이동해야할 값 설정 ( 삼각형의 빗변을 기준으로 x값 ,y값 계산 )
            moveX = (float)(power*Math.cos(theta2));
            moveY = (float)(power*Math.sin(theta2));
            // 0도를 기준으로 x좌표 y좌표를 각각 반전해야 원하는 이동 경로가 나옴
            if (theta >= 0 )
            {
                moveX *= -1;
            }
            else
            {
                moveY *= -1;
            }
            // 이동 범위 설정
            f.setMoveX(moveX);
            f.setMoveY(moveY);
            // 이미지 설정
            f.setImageBitmap(bitmap[BITMAP_FIREBALL]);
            // 회전각 설정
            f.setRotation(theta);
            // 뷰에 추가
            battacklist.add(f);
            touchLo.addView(f);
        }
    }


    private void showdialog()
    {
        dialog = new Dialog(this);

        //다이얼로그의 타이틀 제거
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        //다이얼로그의 자체 배경을 투명하게...
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //다이얼로그에 화면구성 레이아웃을 등록
        dialog.setContentView(R.layout.finish_dialog);

        TextView result = (TextView)dialog.findViewById(R.id.resulttxt);
        TextView moneyTxt = (TextView)dialog.findViewById(R.id.moneyTxt);
        TextView deltext = (TextView)dialog.findViewById(R.id.deltext);

        ImageView getItemimg = (ImageView)dialog.findViewById(R.id.getItemView);

        if (resultGame)
        {
            result.setText("보스에게 승리하였습니다.");
            DBUserInfo.userInfo.setMoney(heromoney + 1000);
            moneyTxt.setText("1000 골드");
            getItemimg.setImageResource(R.mipmap.gold);
        }
        else
        {
            result.setText("보스에게 패배하였습니다.");
            DBUserInfo.userInfo.setHp(DBUserInfo.userInfo.getMaxHp());
            moneyTxt.setText("");
            deltext.setText("");
        }
        finishBtn = (Button) dialog.findViewById(R.id.finishBtn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBUserInfo.userInfo.setHp(heroNowHp);
                Intent i = new Intent();
                i.putExtra("ArrowBossResult", resultGame);
                setResult(RESULT_OK, i);
                dialog.dismiss();
                finish();

            }
        });
        dialog.show();
        isStart = false;

    }

    // 화살과 보스의 충돌체크 메서드
    private void crash()
    {
        // 충돌했는지 감지

        for ( int i = 0; i < arrlist.size();i++)
        {
            Arrow ar = arrlist.get(i);
            // 미사일의 좌표와 보스의 영역을 비교
            if (!ar.isarrow()) continue;
            /*if ((ar.getPositionX()+ar.getWidth()/2 > arrow_boss.getX()+arrow_boss.getWidth()/3 &&
                 ar.getPositionX()+ar.getWidth()/2 < (arrow_boss.getX()+arrow_boss.getWidth()*2/3)) &&
                (ar.getPositionY() > arrow_boss.getY()+arrow_boss.getHeight()/3 &&
                 ar.getPositionY() < (arrow_boss.getY()+arrow_boss.getHeight()*2/3)) )
            */
            if ((ar.getPositionX()+ar.getWidth()/2 > boss_wp.getX() &&
                    ar.getPositionX()+ar.getWidth()/2 < (boss_wp.getX()+boss_wp.getWidth())) &&
                    (ar.getPositionY() > boss_wp.getY()&&
                     ar.getPositionY() < (boss_wp.getY()+boss_wp.getHeight())) )
            {
                // 충돌이 감지된 미사일을 지움
                arrlist.remove(ar);
                touchLo.removeView(ar);
                bossBackColor = Color.RED;
                boss_wp.setBackgroundColor(bossBackColor);
                bossnowHp -= ar.getDamage();


                if (bossnowHp <= 0)
                {
                    resultGame = true;// 승리
                    showdialog();
                }
            }
        }
    }


    private void crashhero()
    {
        // 충돌했는지 감지

        for ( int i = 0; i < battacklist.size();i++)
        {
            FireBall f = battacklist.get(i);
            // 미사일의 좌표와 보스의 영역을 비교
            if ((f.getPositionX() > heroimg.getX()) &&
                    (f.getPositionX() < (heroimg.getX()+heroimg.getHeight())) &&
                    (f.getPositionY() > heroimg.getY()) &&
                            f.getPositionY() < (heroimg.getY()+f.getHeight()) )
            {
                // 충돌이 감지된 미사일을 지움
                battacklist.remove(f);
                touchLo.removeView(f);
                heroBackColor = Color.RED;
                heroimg.setBackgroundColor(heroBackColor);
                heroNowHp -= f.getDamage();
                i--;

                if (heroNowHp <= 0)
                {
                    resultGame = false;//패배

                    showdialog();
                }
            }
        }
    }
    // 버튼 클릭



    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }



    Handler handler = new Handler();

    Runnable imgMove = new Runnable() {
        @Override
        public void run() {
            checkArrow();
            doboss();
            crash();
            checkBossAttack();
            crashhero();

            if (SystemClock.elapsedRealtime() - myBaseTime > myDelayTime) // 15초 보다 크면
            {

                if (gameMode == BOSS_ATTACK)
                {
                    if( battacklist.size() == 0 )
                    {
                        // 적 공격의 객체가 전부 사라지면...
                        gameMode = BOSS_NORMAL;
                        heroimg.setVisibility(View.INVISIBLE);
                        myBaseTime = SystemClock.elapsedRealtime();
                        myDelayTime = 15000;
                    }
                }
                else
                {
                    myDelayTime = 5000;
                    bossAttackcnt = 0;
                    gameMode = BOSS_ATTACK;
                    heroimg.setX(touchX[0]);
                    heroimg.setVisibility(View.VISIBLE);
                    bossWarning.setVisibility(View.VISIBLE);
                    bossWarning.setAnimation(animation);
                    bossWarning.setVisibility(View.INVISIBLE);
                    myBaseTime = SystemClock.elapsedRealtime();
                    myBossAttTime = SystemClock.elapsedRealtime();

                }
            }

            if (gameMode == BOSS_ATTACK)
            {
                // 보스 공격 모드
                if (bossAttackcnt < MAX_BOSS_ATTACK )
                {
                    if (( SystemClock.elapsedRealtime() - myBossAttTime) > 800) {
                        bossAttack((int) arrow_boss.getX()+arrow_boss.getWidth()/2, (int) arrow_boss.getY()+arrow_boss.getHeight()/2);
                        myBossAttTime =  SystemClock.elapsedRealtime();
                    }
                }
            }
            // 보스 이미지 애니메이션 ( 비트맵 )
            if (++animDelay>10 )
            {
                animNum++;
                animNum%=6;
                arrow_boss.setImageBitmap(anim[animNum]);
                animDelay = 0;
            }
            if (isStart)
                handler.postDelayed(imgMove , 10);
                //imgMove.sendEmptyMessageDelayed(0, 10);
        }
    };

   /* Handler imgMove = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

        }
    };*/
    Runnable dmgHandler = new Runnable() {
       @Override
       public void run() {
           if (bossViewHp > bossnowHp)
           {
               if (bossViewHp - bossnowHp < 20 )
               {
                   bossViewHp = bossnowHp;
               }
               else
                   bossViewHp-= 20;
               bosshp.setLayoutParams(new LinearLayout.LayoutParams(width * bossViewHp / bossMaxHp, ViewGroup.LayoutParams.WRAP_CONTENT));

           }
           else
           {
               if (bossBackColor != 0)
               {
                   bossBackColor = 0;
                   boss_wp.setBackgroundColor(bossBackColor);
               }

           }

           if (heroViewHp > heroNowHp)
           {

               if (heroViewHp - heroNowHp < 20 )
               {
                   heroViewHp = heroNowHp;
               }
               else
                   heroViewHp-= 20;
               herohp.setLayoutParams(new LinearLayout.LayoutParams(width * heroViewHp / heroMaxHp, ViewGroup.LayoutParams.WRAP_CONTENT));
           }
           else
           {

               if (heroBackColor != 0)
               {
                   heroBackColor = 0;
                   heroimg.setBackgroundColor(bossBackColor);
               }
           }
           //if ( (heroViewHp != heroNowHp) || (bossViewHp != bossnowHp) )
           {
               handler.postDelayed(dmgHandler, 10);
           }
       }
   };

    Runnable finishBoss = new Runnable() {
        @Override
        public void run() {
            resultGame = true;
            showdialog();
        }
    };
}



