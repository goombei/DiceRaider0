package user.test.ex.diceraider0;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import user.test.ex.db.DBUserInfo;


public class MainActivity extends AppCompatActivity
        implements Archive,
        DialogMainFragment.DialogResultLinker,
        WallView.WallLinker{

    SharedPreferences pref;

    DialogMainFragment dialogDiceFragment;
    Bundle args;

    WallView wallView;
    Dice dice;

    DBUserInfo loadDB;

    SoundManager sm;
    int soundRes;

    RelativeLayout layout_board;
    HorizontalScrollView hScrollView;
    Button btn_dice, btn_inven, btn_map, btn_setting, btn_test, btn_rank, btn_hp;
    TextView txt_att;
    LinearLayout layout_top;

    boolean catchedEvent; // 이벤트 중복 처리 방지

    boolean catchedBack; // 뒤로가기 두번 체크
    boolean movedMap; // 맵 이동시 음악 정지 방지용 true: 맵으로, false: 다른 액티비티로

    int test_mode = TEST_NO;
    int test_stage = TEST_STAGE1;
    int test_chance = CHANCE_TO_GET_KEY_ELITE;
    boolean test_boss_stage1_pass;

    boolean idol = TEST_IDOL_FALSE;

    boolean reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor edit = pref.edit();

        loadDB = new DBUserInfo(this); // DB호출
        loadDB.itemSetting(); // 아이템 능력치 세팅//아이템 능력치 세팅

        wallView = new WallView(this);
        dice = new Dice();
        args = getIntent().getExtras();
        reset = args.getBoolean("reset", false);
        // 랭킹에서 넘어온 데이터를 받음
        if (reset){
            loadDB.initDBData();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_board);

        //사이즈
        WindowManager wm = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        //가로스크롤뷰에 화면 적용
        hScrollView = (HorizontalScrollView)findViewById(R.id.hScrollView);
        layout_board = (RelativeLayout) findViewById(R.id.layout_board);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(wallView.getRoadWidth(), ViewGroup.LayoutParams.MATCH_PARENT);
        layout_board.addView(wallView, params);

        LinearLayout.LayoutParams params_view = new LinearLayout.LayoutParams(dm.widthPixels / 7, dm.widthPixels / 7);
        LinearLayout.LayoutParams params_dice = new LinearLayout.LayoutParams(dm.heightPixels / 6, dm.heightPixels / 6);

        layout_top = (LinearLayout)findViewById(R.id.layout_top);

        //화면 하단 주사위 버튼
        btn_dice = setButton(R.id.btn_dice, params_dice);

        //상단 버튼 및 텍스트뷰
        btn_inven = setButton(R.id.btn_inven, params_view);
        btn_map = setButton(R.id.btn_map, params_view);
        btn_setting = setButton(R.id.btn_setting, params_view);
        btn_test = setButton(R.id.btn_test, params_view);
        btn_rank = setButton(R.id.btn_rank, params_view);
        btn_hp = setButton(R.id.btn_hp, params_view);

        txt_att = setTextView(R.id.txt_att, params_view);

        //내용 갱신
        refreshStatus();

        //사운드매니저
        sm = new SoundManager(this);
        soundRes = sm.BGM_MAIN_ACTS[wallView.getCurrStage()];
    } // onCreate()


    @Override
    protected void onResume() {
        super.onResume();
        //Activity를 들어오고 나가서나 종료할 때 상황에서 DBUserInfo의 모든 정보를 DB에 저장한다
        loadDB.allSettingDB();
        refreshStatus();

        if (soundRes != 0 && !movedMap){
            sm.playBGM(soundRes, true);
        }
        movedMap = false;
    } // onResume()


    @Override
    protected void onPause() {
        super.onPause();
        //Activity를 들어오고 나가서나 종료할 때 상황에서 DBUserInfo의 모든 정보를 DB에 저장한다
        loadDB.allSettingDB();
        //DB를 닫는다.
        if (!movedMap){
            sm.stopBGM();
        }


    } // onPause()


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity를 들어오고 나갈 때 DBUserInfo의 모든 정보를 DB에 저장한다
        //onDestroy()를 오기 전에 onPause()를 거치므로 여기서는 필요없다
        // loadDB.allSettingDB();
        handler.removeCallbacksAndMessages(null);
        loadDB.closeDB();
        wallView.remove();
        System.gc();
    } // onDestroy()


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

            switch( requestCode)
            {
                case RETURN_ARROW_BOSS : // 화살 보스에서 넘어왔을시...
                    boolean resultboss = data.getBooleanExtra("ArrowBossResult" , false);

                    if (resultboss)
                    {
                        // 엔딩
                        // Toast.makeText(ainActivity.this, "승리", Toast.LENGTH_SHORT).show();
                        // dialog.showReward(rewardItem, rewardMoney, wallView.getCurrStage());

                        // data.putExtra("mode", MODE_REWARD);
                        // data.putExtra("stage", wallView.getCurrStage());
                        data.putExtra("mode", MODE_END_STAGE);
                        data.putExtra("stage", wallView.getCurrStage());
                    }
                    else
                    {
                        // 맨처음 돌아가기
                        // Toast.makeText(ainActivity.this, "실패", Toast.LENGTH_SHORT).show();
                        // dialog.showRestartStage();
                        data.putExtra("mode", MODE_RESTART);

                    }
                    showDialog(data.getExtras());
                    break;

                case RETURN_BOSS_JUNGLE: // 덩굴 보스에서 넘어옴
                    resultboss = data.getBooleanExtra("StageClear", false);
                    int hp = data.getIntExtra("hp", 0);
                    loadDB.userInfo.setHp(hp);

                    if (resultboss) {
                        data.putExtra("mode", MODE_REWARD);
                        data.putExtra("stage", wallView.getCurrStage());
                        showDialog(data.getExtras());
                    }

                    refreshStatus();

                    break;

                case RETURN_BOSS_TOMB_KEY: // 피라미드 보스에서 넘어옴
                    resultboss = data.getBooleanExtra("StageClear", false);

                    if (resultboss) {
                        Intent i = new Intent(MainActivity.this, BossSudokuActivity.class);
                        i.putExtra("boss1_thrupass", test_boss_stage1_pass);
                        startActivityForResult(i, RETURN_BOSS_TOMB_PUZZLE);
                    } else {
                        data.putExtra("mode", MODE_RESTART);
                        showDialog(data.getExtras());
                    }

                    break;

                //sudoku
                case RETURN_BOSS_TOMB_PUZZLE:
                    resultboss = data.getBooleanExtra("StageClear", false);

                    if (resultboss) {
                        data.putExtra("mode", MODE_REWARD);
                        data.putExtra("stage", wallView.getCurrStage());
                        showDialog(data.getExtras());
                    } else {
                        data.putExtra("mode", MODE_RESTART);
                        showDialog(data.getExtras());
                    }
                    break;

                case RETURN_ACTION:
                    int userHP = data.getIntExtra("userHP", 0);
                    DBUserInfo.userInfo.setHp(userHP); //DB에 현재 HP 저장
                    data.putExtra("mode", Archive.MODE_END_ACTION);
                    showDialog(data.getExtras());

                    break;

                case RETURN_RANK:
                    Intent i = new Intent(MainActivity.this, StartActivity.class);
                    // startActivityForResult(i, RETURN_TITLE);
                    Bundle b = data.getExtras();

                    Log.i("MY", "리절트" + b.getBoolean("reset"));
                    i.putExtras(b);
                    startActivity(i);
                    finish();

                    break;

                case RETURN_TITLE:
                    boolean reset = data.getBooleanExtra("reset", false);
                    if (reset) {
                        newGame();
                    } else {

                    }
                    break;

                default :
                    break;
            }
        }
    } // onActivityResult()


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (catchedBack) {
                finish();
            } else {
                catchedBack = true;
                handler.postDelayed(shutdown, 1000);
                Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            }
        }

        return false;
    }


    //주사위
    @Override
    public int rollDice() {
        int diceResult = dice.roll(MAX_6);
        refreshStatus();

        return diceResult;
    } // rollDice()


    //주사위 다이얼로그 종료 후 동작
    @Override
    public void move() {
        if (test_mode == TEST_NO){

            wallView.setNum_move(dice.getDiceResult());
            wallView.move();
            catchedEvent = false;

        } else {
            btnsEnabled(true);
            switch (test_mode) {
                case TEST_BOSS:
                    catchEvent(BOSS, test_stage);
                    break;

                case TEST_ACTION:
                    catchEvent(PLAIN, test_stage);
                    break;

                case TEST_ELITE:
                    catchEvent(ANTAGONIST, test_stage);
                    break;

                case TEST_RANK:
                    wallView.setCurrStage(test_stage);
                    stageUp();
                    break;

                case TEST_TRAP:
                    catchEvent(TRAP, test_stage);
                    break;

                case TEST_MONEY:
                    catchEvent(TREASURE, test_stage);
                    break;

                case TEST_SHOP:
                    catchEvent(SHOP, test_stage);
                    break;
            }
        }

    } // move()


    // 이동 후 작업
    @Override
    public void afterMove() {

        if (!catchedEvent) {
            catchEvent(wallView.getNewEvent(), wallView.getCurrStage());
            catchedEvent = true;
        } else {
            Log.i("MY", "not this way");
        }
        btnsEnabled(true);
        refreshStatus();
    } // afterMove()


    // 보스 종료 후 보상 창 닫은 후
    @Override
    public void rewardOK() {

        args.clear();
        args.putInt("mode", MODE_END_STAGE);
        args.putInt("stage", wallView.getCurrStage());
        showDialog(args);
    } // rewardOK()


    // 스테이지 변경
    @Override
    public void stageUp() {

        int stage = wallView.getCurrStage() + 1;

        if (stage >= STAGE_MAX) {
            // stageRestart();
            args.clear();
            args.putInt("roll", dice.getNum_rolled());
            args.putBoolean("reset", true);

            Intent intent = new Intent(MainActivity.this, RankActivity.class);
            intent.putExtras(args);
            startActivityForResult(intent, RETURN_RANK);

        } else {
            sm.stopBGM();
            soundRes = sm.BGM_MAIN_ACTS[stage];
            sm.playBGM(soundRes, true);

            idol = TEST_IDOL_FALSE;
            wallView.setCurrStage(stage);
            wallView.stageUp();
        }
    } // stageUp()


    // 다시 시작
    @Override
    public void stageRestart() {
        btnsEnabled(false);
        wallView.setIsRestart(true);
        wallView.move();
    } // stageRestart()


    // 상단 영웅 정보 갱신
    @Override
    public void refreshStatus() {

        txt_att.setText(loadDB.userInfo.getAtt());

        if (loadDB.userInfo.getHpParseInt() <= 0){
            loadDB.userInfo.setHp(loadDB.userInfo.getMaxHp());
            args.clear();
            args.putInt("mode", MODE_RESTART);
            showDialog(args);
        }
        btn_hp.setText(loadDB.userInfo.getHp());
        if (loadDB.checkEquip(loadDB.SLOT_EQUIP_POTION)) {
            btn_hp.setBackgroundResource(R.mipmap.big_portion);
        } else {
            btn_hp.setBackgroundResource(R.mipmap.armor1);
        }


        String str_roll = String.format(Locale.getDefault(), "%d", dice.getNum_rolled());
        btn_rank.setText(str_roll);
    }

    @Override
    public void setTest(int test_mode, int test_stage, int test_chance, boolean thrupass) {
        this.test_mode = test_mode;
        this.test_stage = test_stage;
        this.test_chance = test_chance;
        this.test_boss_stage1_pass = thrupass;
    }

    @Override
    public void sendBundle(Bundle bundle) {

        idol = (idol) ?
                idol : bundle.getBoolean("idol", false);
    }


    //핸들러: 3초 안 뒤로가기 더 누르면 종료
    Handler handler = new Handler();
    Runnable shutdown = new Runnable() {
        @Override
        public void run() {
            catchedBack = false;
        }
    };


    //각 버튼에 대한 동작
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            args.clear();
            switch (view.getId()) {
                case R.id.btn_dice:
                    hScrollView.setScrollX(0);

                    btnsEnabled(false);
                    if (0 < loadDB.userInfo.getAttParseInt()){
                        args.putInt("mode", MODE_DICE);
                        showDialog(args);
                    } else {
                        Toast.makeText(getApplicationContext(), "Check your weapon", Toast.LENGTH_SHORT).show();
                        btnsEnabled(true);
                    }

                    break;

                case R.id.btn_inven:
                    args.clear();
                    args.putInt("stage", wallView.getCurrStage());
                    args.putBoolean("pass", idol);
                    Intent i = new Intent(MainActivity.this, InventoryActivity.class);
                    i.putExtras(args);
                    startActivity(i);
                    break;

                case R.id.btn_map:
                    movedMap = true;
                    i = new Intent(MainActivity.this, MapActivity.class);
                    args.putInt("playerPosition", wallView.getLocation_hero());
                    args.putInt("stage", wallView.getCurrStage());
                    i.putExtras(args);
                    startActivity(i);
                    break;

                case R.id.btn_setting:
                    SettingDialog settingDialog = new SettingDialog(MainActivity.this, callBack);
                    settingDialog.show();
                    break;

                case R.id.btn_test:
                    if (BTN_TEST_ENABLED) {
                        args.clear();
                        args.putInt("mode", MODE_TEST);
                        showDialog(args);
                    }
                    break;

                case R.id.btn_hp:
                    // 포션 장착시 / 없을시 이미지 변경
                    // 포션 장착시 기능(회복)
                    // 사용 후 유저 정보에 갱신
                    loadDB.usePotion();
                    refreshStatus();

                    break;

                case R.id.btn_rank:
                    args.clear();
                    args.putBoolean("reset", false);
                    i = new Intent(MainActivity.this, RankActivity.class);
                    i.putExtras(args);
                    startActivity(i);
                    break;
            }

        }
    }; // click


    // 버튼 등록 및 설정
    private Button setButton(int res, LinearLayout.LayoutParams params) {

        Button button = (Button)findViewById(res);
        button.setLayoutParams(params);
        button.setTextSize(params.width / 8);
        button.setOnClickListener(click);

        return button;
    }


    // 텍스트뷰 등록 및 설정
    private TextView setTextView(int res, LinearLayout.LayoutParams params) {

        TextView textView = (TextView)findViewById(res);
        textView.setLayoutParams(params);
        textView.setTextSize(params.width / 8);

        return textView;
    }

    private void btnsEnabled(boolean isEnable) {
        float alpha = (isEnable) ?
                1 : .5f;

        Button[] btns = {
                btn_dice,
                btn_inven,
                btn_map,
                btn_setting,
                btn_test,
                btn_rank};
        for (int i = 0; i < btns.length; i++){
            btns[i].setAlpha(alpha);
            btns[i].setEnabled(isEnable);
        }

    }


    // 다이얼로그 띄움
    private void showDialog(Bundle args) {

        dialogDiceFragment = DialogMainFragment.newInstance(args);
        dialogDiceFragment.show(getFragmentManager(), "");
    } // showDialog()


    private void newGame() {

        layout_board.removeView(wallView);
        dice.reset();
        wallView.remove();
        wallView = new WallView(MainActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                wallView.getRoadWidth(),
                ViewGroup.LayoutParams.MATCH_PARENT );
        layout_board.addView(wallView, params);

        refreshStatus();
    }


    // 이벤트 전달
    public boolean catchEvent(int infoTile, int currStage){
        args.clear();
        args.putInt("stage", currStage);


        // event와 메서드 연결
        switch (infoTile) {
            // PLAIN    TREASURE    TRAP    SHOP   ANTAGONIST
            // 0        1           2       3       4
            case PLAIN:
                doEventPlain();
                break;

            case TREASURE:
                doEventTreasure();
                break;

            case TRAP:
                doEventTrap();
                break;

            case SHOP:
                doEventShop();
                break;

            case ANTAGONIST:
                doEventAntagonist(true);
                break;

            case BOSS:
                if (idol) {
                    doEventBoss(currStage);
                } else {
                    args.putInt("mode", MODE_RESTART);
                    showDialog(args);
                }
                break;

            case INVALID:
            default:
                return false;
        }

        return true;
    } // catchEvent()


    // 잉여 칸
    private void doEventPlain() {
        //잉여칸: 현재 적 이벤트로 연결
        doEventAntagonist(false);
    } // doEventPlain()


    // 보물 칸
    private void doEventTreasure() {

        args.putInt("mode", DialogMainFragment.MODE_TREASURE);
        showDialog(args);
    } // doEventTreasure()


    // 함정 칸
    private void doEventTrap() {

        args.putInt("mode", DialogMainFragment.MODE_TRAP);
        showDialog(args);
    } // doEventTrap()


    // 상점 칸
    private void doEventShop() {
        args.putBoolean("pass", idol);

        Intent intent = new Intent( MainActivity.this, ShopActivity.class );  //PlainActivity로 연결
        intent.putExtras(args);
        startActivity(intent);

    } // doEventShop()


    // 적 칸
    private void doEventAntagonist(boolean elite) {

        args.clear();
        args.putBoolean("elite", elite);
        args.putInt("chance", test_chance);

        Intent intent = new Intent( MainActivity.this, ActionActivity.class );  //ActionActivity로 연결
        intent.putExtras(args);
        startActivityForResult(intent, RETURN_ACTION);
        // startActivity(intent);

    } // doEventAntagonist()


    // 보스 칸
    private void doEventBoss(int currStage) {

        args.putBoolean("boss1_thrupass", test_boss_stage1_pass);

        Intent intent;
        int return_value;

        if (currStage == StageInfo.STAGE_THREE) {

            intent = new Intent(MainActivity.this, ArrowBossActivity.class );
            // 콜백 인텐트
            return_value = RETURN_ARROW_BOSS;

        } else if (currStage == StageInfo.STAGE_TWO){
            intent = new Intent (MainActivity.this, BossActivity.class); //BossActivity로 연결
            return_value = RETURN_BOSS_JUNGLE;

            args.putBoolean("knife", idol);
            args.putInt("maxhp", loadDB.userInfo.getMaxHp());
            args.putInt("hp", loadDB.userInfo.getHpParseInt());

        } else {

            intent = new Intent (MainActivity.this, KeyActivity.class); //Boss로 연결
            return_value = RETURN_BOSS_TOMB_KEY;
        }
        intent.putExtras(args);
        startActivityForResult(intent, return_value);
    } // doEventBoss()

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
