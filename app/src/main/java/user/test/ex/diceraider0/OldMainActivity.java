package user.test.ex.diceraider0;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import user.test.ex.db.DBUserInfo;

public class OldMainActivity extends AppCompatActivity implements Archive{

    HorizontalScrollView scroll_wall;
    LinearLayout linear, subLinear;
    Button rollBtn, invenBtn, MapBtn, shopBtn, setting, rankBtn;

    EventDialog dialog;
    WallView wallView;

    boolean rollBtnOff;

    SoundManager sm;
    int soundRes;

    DBUserInfo loadDB;

    Bundle bundleMap;  //전체맵(유저 위치와 액트 정보를 담을 번들

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //DB호출
        loadDB = new DBUserInfo(this);

        //아이템 능력치 세팅
        loadDB.itemSetting();

        setContentView(R.layout.activity_main);

        wallView = new WallView(this);
        dialog = new EventDialog(OldMainActivity.this, wallView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(wallView.getRoadWidth(), ViewGroup.LayoutParams.MATCH_PARENT);

        scroll_wall = (HorizontalScrollView)findViewById(R.id.scroll_wall);
        rollBtn = (Button)findViewById(R.id.rollBtn);
        rollBtn.setOnClickListener(click);

        invenBtn = (Button)findViewById(R.id.invenBtn);
        invenBtn.setOnClickListener(click);

        MapBtn = (Button)findViewById(R.id.mapBtn);
        MapBtn.setOnClickListener(click);

        shopBtn = (Button)findViewById(R.id.shopBtn);
        shopBtn.setOnClickListener(click);

        setting = (Button)findViewById(R.id.setting);
        setting.setOnClickListener(click);

        rankBtn = (Button)findViewById(R.id.rankBtn);
        rankBtn.setOnClickListener(click);

        linear = new LinearLayout(this);
        linear.setLayoutParams(params);
        linear.setOrientation(LinearLayout.HORIZONTAL);

        subLinear = new LinearLayout(this);
        subLinear.setLayoutParams(params1);
        subLinear.setOrientation(LinearLayout.HORIZONTAL);

        scroll_wall.addView(linear);
        linear.addView(subLinear);
        subLinear.addView(wallView);

        sm = new SoundManager(this);
        soundRes = sm.BGM_MAIN_ACTS[wallView.getCurrStage()];

    }//onCreate()

    @Override
    protected void onResume() {
        super.onResume();
        //Activity를 들어오고 나가서나 종료할 때 상황에서 DBUserInfo의 모든 정보를 DB에 저장한다
        loadDB.allSettingDB();

        if (soundRes != 0){
            sm.playBGM(soundRes, true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Activity를 들어오고 나가서나 종료할 때 상황에서 DBUserInfo의 모든 정보를 DB에 저장한다
        loadDB.allSettingDB();
        //DB를 닫는다.
        sm.stopBGM();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity를 들어오고 나갈 때 DBUserInfo의 모든 정보를 DB에 저장한다
        //onDestroy()를 오기 전에 onPause()를 거치므로 여기서는 필요없다
        // loadDB.allSettingDB();
        loadDB.closeDB();

        handler.removeCallbacksAndMessages(null);

        //호건이형 이부분 dialog 생성 안되있으면 터져서 이렇게 바꿨어요~~~
        dialog.remove();

        wallView.remove();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // 요청코드 구분 -> Archive 인터페이스 확인
        // ArrowBossActivity 543줄  myButtonClick 예시 확인 ( intent , result결과값 전송 )
        // EventDialog  193줄 Tile.BOSS : StartActivityForResult 확인

        if(resultCode == RESULT_OK)
        {
            // 디비 저장
            loadDB.allSettingDB();

            int rewardMoney = 0;
            String rewardItem = "";

            switch( requestCode)
            {
                case RETURN_ARROW_BOSS : // 화살 보스에서 넘어왔을시...
                    boolean resultboss = data.getBooleanExtra("ArrowBossResult" , false);

                    if (resultboss)
                    {
                        // 엔딩
                        // Toast.makeText(ainActivity.this, "승리", Toast.LENGTH_SHORT).show();
                        dialog.showReward(rewardItem, rewardMoney, wallView.getCurrStage());
                    }
                    else
                    {
                        // 맨처음 돌아가기
                        // Toast.makeText(ainActivity.this, "실패", Toast.LENGTH_SHORT).show();
                        dialog.showRestartStage();
                    }
                    break;

                case RETURN_BOSS_JUNGLE: // 덩굴 보스에서 넘어옴
                    resultboss = data.getBooleanExtra("StageClear", false);
                    rewardMoney = data.getIntExtra("rewardMoney", 0);
                    rewardItem = data.getStringExtra("rewardItem");
                    if (resultboss) {
                        dialog.showReward(rewardItem, rewardMoney, wallView.getCurrStage());
                    } else {
                        dialog.showRestartStage();
                    }
                    break;

                case RETURN_BOSS_TOMB_KEY: // 피라미드 보스에서 넘어옴
                    resultboss = data.getBooleanExtra("StageClear", false);
                    if (resultboss) {
                        Intent i = new Intent(OldMainActivity.this, BossSudokuActivity.class);
                        startActivityForResult(i, RETURN_BOSS_TOMB_PUZZLE);
                    } else {
                        dialog.showRestartStage();
                    }

                    break;

                case RETURN_BOSS_TOMB_PUZZLE:
                    resultboss = data.getBooleanExtra("StageClear", false);
                    rewardMoney = data.getIntExtra("rewardMoney", 0);
                    rewardItem = data.getStringExtra("rewardItem");

                    if (resultboss) {
                        dialog.showReward(rewardItem, rewardMoney, wallView.getCurrStage());
                    } else {
                        dialog.showRestartStage();
                    }
                    break;

                default :
                    break;
            }

            handler.post(checkStageEnd);
        }

    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent;
            switch (view.getId()) {
                case R.id.rollBtn:
                    rollBtn.setEnabled(false);
                    scroll_wall.scrollTo(0, 0);

                    dialog.showDice();
                    handler.post(checkEvent);
                    // handler.post(checkDice);
                    break;

                case R.id.invenBtn:
                    intent = new Intent(OldMainActivity.this, InventoryActivity.class);
                    startActivity(intent);
                    break;

                case R.id.mapBtn:
                    bundleMap = new Bundle();
                    intent = new Intent(OldMainActivity.this, MapActivity.class);
                    bundleMap.putInt("playerPosition", wallView.getLocation_hero());
                    bundleMap.putInt("stage", wallView.getCurrStage());
                    intent.putExtras(bundleMap);
                    startActivity(intent);
                    break;

                case R.id.shopBtn:
                    intent = new Intent(OldMainActivity.this, ShopActivity.class);
                    startActivity(intent);
                    break;

                case R.id.setting:
                    SettingDialog settingDialog = new SettingDialog(OldMainActivity.this, callBack);
                    settingDialog.show();
                    break;

                case R.id.rankBtn:
                    intent = new Intent(OldMainActivity.this, RankActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    };


    Handler handler = new Handler();

    Runnable checkEvent = new Runnable() {
        @Override
        public void run() {
            if (wallView.isMoving() && !rollBtnOff )
            {
                // offStart
                rollBtnOff = true;
                handler.postDelayed(this, 1000 * wallView.getNum_move() + 400);
            }
            else {
                if (rollBtnOff) {
                    rollBtnOff = false;
                    rollBtn.setEnabled(true);
                    /*Toast.makeText(
                            getApplicationContext(),
                            evStr[dialog.eventCatch(wallView.getNewEvent(),wallView.getCurrStage())]
                                    + " " + wallView.getLocation_hero(),
                            Toast.LENGTH_SHORT ).show();*/
                    dialog.eventCatch(wallView.getNewEvent(),wallView.getCurrStage());
                    handler.removeCallbacks(this);

                } else {
                    handler.postDelayed(this, 100);
                }
            }
        }
    };


    Runnable checkDice = new Runnable() {
        @Override
        public void run() {
            if (dialog.isDiceEnd()) {
                handler.removeCallbacks(this);
                dialog.setDiceEnd(false);
                /*
                Toast.makeText(
                        getApplicationContext(),
                        evStr[dialog.eventCatch(wallView.getNewEvent(),wallView.getCurrStage())]
                                + " " + wallView.getLocation_hero(),
                        Toast.LENGTH_SHORT ).show();*/

                wallView.setNum_move(dialog.dice.getDiceResult());
                handler.postDelayed(enableBtn, 1000 * wallView.move());
            } else {
                handler.postDelayed(this, 100);
            }
        }
    };

    Runnable enableBtn = new Runnable() {
        @Override
        public void run() {
            rollBtn.setEnabled(true);
            dialog.eventCatch(wallView.getNewEvent(), wallView.getCurrStage());
        }
    };


    Runnable checkStageEnd = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 100);

            if (dialog.isStageEnd()){
                handler.removeCallbacks(this);

                dialog.setStageEnd(false);
                int stage = wallView.getCurrStage() + 1;
                stage = (stage >= STAGE_MAX) ?
                        STAGE_MAX - 1 : stage;

                sm.stopBGM();
                soundRes = sm.BGM_MAIN_ACTS[stage];
                sm.playBGM(soundRes, true);

                wallView.setCurrStage(stage);
                wallView.stageUp();
            }

            if (dialog.isStageRestart()) {
                handler.removeCallbacks(this);

                dialog.setStageRestart(false);

                wallView.setIsRestart(true);
                wallView.move();
            }
        }
    };

    //세팅에서 Sound On/off 후에 즉각적인 반응을 하기 위해서 만든 콜백
    SettingDialog.SettingCallBack callBack = new SettingDialog.SettingCallBack() {
        @Override
        public void soundSetting() {
            Boolean isBgmPlay;
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            isBgmPlay = pref.getBoolean("isBgmPlay", true);

            //음악을 실행해라
            if(isBgmPlay && soundRes != 0) {
                sm.playBGM(soundRes, true);
            } else {
                sm.stopBGM();
            }

        }
    };


}