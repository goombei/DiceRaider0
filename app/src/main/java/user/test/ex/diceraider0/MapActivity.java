package user.test.ex.diceraider0;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MapActivity extends AppCompatActivity {

    Button mapBtn;
    RelativeLayout map_test;
    int playerPosition;
    int stageInfo;

    int frameX, frameY;
    Position[] position = new Position[20];
    Boolean ButtonInit = false;
    ImageButton playerImage;

//    SoundManager sm;
//    int soundRes;

    float incX, incY;  //x,y 증가 배수



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        map_test = (RelativeLayout)findViewById(R.id.map_test);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        //값 추출
        playerPosition = bundle.getInt("playerPosition");
        stageInfo = bundle.getInt("stage");

        //배경화면 스테이지 정보에 따라 변경
        switch(stageInfo){
            case 0:
                map_test.setBackgroundResource(R.mipmap.map3);
                break;
            case 1:
                map_test.setBackgroundResource(R.mipmap.map4);
                break;
            case 2:
                map_test.setBackgroundResource(R.mipmap.map5);
                break;
        }

//        playerPosition = i.getIntExtra("playerPosition", -1 );
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//        //BGM 메인 세팅
//        sm = new SoundManager(this);
//        soundRes = sm.BGM_MAIN_ACTS[stageInfo];

        WindowManager wm = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        int pWidth = dm.widthPixels / 5;
        int pHeight = dm.heightPixels / 6;

        playerImage = new ImageButton(MapActivity.this);
        playerImage.setVisibility(View.INVISIBLE);
        playerImage.setLayoutParams(new RelativeLayout.LayoutParams(pWidth, pHeight));
        playerImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        playerImage.setImageResource(R.mipmap.hero01);

        playerImage.setBackgroundColor(0x00000000);

        if(playerPosition > position.length -1) playerPosition = 0;



        // 배경 좌표 잡기 및 이미지 버튼 생성
        for (int idx = 0; idx < position.length; idx++) {
            position[idx] = new Position();
//            position[idx].position = new ImageButton(MapActivity.this);
//            position[idx].position.setId(idx);// 버튼에 아이디 추가


//                position[idx].position.setVisibility(View.INVISIBLE);
//            if(idx==0 || idx==7 || idx==13 || idx==19 )  //대표 이벤트 좌표찍기
//                position[idx].position.setVisibility(View.VISIBLE);


            //맵 좌표 테스트 코드
//            if(idx == 12)
//                position[idx].position.setVisibility(View.INVISIBLE);

        }


        mapBtn = (Button)findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }//onCreate()

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        // 시작될때 버튼이 1번 초기화 된다.
        if (!ButtonInit) CreateButtonInit();
    }//onWindowFocusChanged


    private void CreateButtonInit() {

        ButtonInit = true;
        frameX = map_test.getWidth();
        frameY = map_test.getHeight();
        Log.i("x", "" + frameX);
        Log.i("y", "" + frameY);


//        //플레이어 이미지 크기 변환
//        int x = (int)frameX;
//        int y = (int)frameY;
//        hero = BitmapFactory.decodeResource(getResources(), R.mipmap.hero01);
//        hero = Bitmap.createScaledBitmap(hero, x/10, y/10, false);

        //스핑크스, 늑대인간, 피라미드, 시작위치 좌표 설정
        position[0].x = frameX / 10.97f * (-1);
        position[0].y = frameY * 10 / 13;
        position[7].x = frameX * 10 / 11;
        position[7].y = frameY * 0.36f;
        position[13].x = frameX * 0.0599f;
        position[13].y = frameY * 0.1859f;
        position[19].x = frameX * 0.7917f;
        position[19].y = frameY * 0.0470f * (-1);

//        position[0].x = -70;
//        position[0].y = frameY*10/13;
//        position[7].x = frameX*10/11;
//        position[7].y = frameY*6/13 -50 -50;
//        position[13].x = frameX/8 -50;
//        position[13].y = frameY/3 -100 -50;
//        position[19].x = frameX*6/7 -50;
//        position[19].y = frameY/10 -100 -50;


        incX = (position[7].x - position[0].x) / 8.0f;
        incY = (position[7].y - position[0].y) / 8.0f;
//        slope = incY/incX;

        for (int i = 0; i < 8; i++) {
            position[i].x = incX * i + position[0].x;
            position[i].y = incY * i + position[0].y;
//            Log.i("MY", ""+position[i].y);

        }


        incX = (position[13].x - position[7].x) / 6.0f;
        incY = (position[13].y - position[7].y) / 6.0f;
//        slope = incY/incX;

        for (int i = 0; i < 6; i++) {
            position[7 + i].x = incX * i + position[7].x;
            position[7 + i].y = incY * i + position[7].y;
//            Log.i("MY", ""+position[i].y);

        }


        incX = (position[19].x - position[13].x) / 6.0f;
        incY = (position[19].y - position[13].y) / 6.0f;
//        slope = incY/incX;

        for (int i = 0; i < 6; i++) {
            position[13 + i].x = incX * i + position[13].x;
            position[13 + i].y = incY * i + position[13].y;
//            Log.i("MY", ""+position[i].y);

        }


//        for (int idx = 0; idx < position.length; idx++) {
//            position[idx].position.setX(position[idx].x);
//            position[idx].position.setY(position[idx].y);
//            map_test.addView(position[idx].position);
//            Log.i("x", "" + position[idx].x);
//            Log.i("y", "" + position[idx].y);
//    }


        for (int idx = 0; idx < position.length; idx++) {
            if (idx == playerPosition) {
                map_test.addView(playerImage);

                playerImage.setX(position[idx].x);
                playerImage.setY(position[idx].y);

                Log.i("MY", "x : " + playerImage.getX());
                Log.i("MY", "y : " + playerImage.getY());

                playerImage.setVisibility(View.VISIBLE);

            }
        }



    }//CreateButtonInit


//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (soundRes != 0){
//            sm.playBGM(soundRes, true);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        sm.stopBGM();
//    }


}
