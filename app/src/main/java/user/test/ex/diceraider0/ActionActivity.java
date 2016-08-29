package user.test.ex.diceraider0;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssomai.android.scalablelayout.ScalableLayout;

import java.util.Random;

import user.test.ex.db.DBUserInfo;
import user.test.ex.db.MonsterInfo;

public class ActionActivity extends AppCompatActivity {

    int act;//스테이지 정보
    //int width;
    boolean elite = false;
    int test_chance;
    FrameLayout frameLayout;
    Display display;
    ImageView enemyHpImgView, heroHpImgView, diceimage;
    TextView dealtxt;
    ScalableLayout scalalo;
    //Dialog actionend;
    ImageView enemyimage, enemydiceimage;
    int diecount;

    //테두리 효과
    ImageView user_border, enemy_border;

    /*//전투 종료 다이얼로그 객체
    ImageView getItemView;
    TextView moneyTxt, resultTxt;*/

    int monster_selRnd = 0;

    int actioncount; //주사위 굴린수

    //int winRate = 1;//이길 확률 구분

    int heroDice; // 유저 다이스
    int enemyDice; // 적 다이스

    //공격,방어 및 포션 활용
    ImageView attackImage, defenceImage, position, emptyposition;
    float drink;
    float userlimitHP;//유저 전체 에너지바
    int attackdamage;

    float saveMaxHP;
    float saveHP;

    float heroAttDamage = 1;//내가 주는 데미지 값 저장
    float enemyAttDamage = 1;//적이 주는 데미지 값 저장
    float maxenemyView;//적 전체 에너지바

    //에너지바
    float enemyViewtop;
    float enemyViewleft;
    float enemyViewheight;
    float userViewtop;
    float userViewleft;
    float userViewheight;


    float userMaxHp; //유저 최대 체력 수치값
    float userHP;    //유저 현재 체력 수치값
    float maxusermyView; //유저 전체 에너지바


    int turn = 0;//턴 구별

    //적과 아군의 체력
    float enemyMaxHP;  //최대 체력 수치값
    float enemyHP;  //현재 체력 수치값


    TextView userNowHPtxt, userHPtxt;

    //적과 아군 이미지뷰
    ImageView user_img, enemy_img;

    //주사위 이미지
    AnimationDrawable ani_dice, enemy_dice, vibration_enemy, vibration_user;
    int diceCount = 0;


    // 초기화값 지정
    boolean init;

    // 생성초기화
    boolean oncreateinit;

    //사운드 담당
    SoundManager sm;
    Context context;

    int[] monsterSound;

    String settingPortion = "";
    String settingWhip = "";

    String[] userSetItems = new String[4];
    String[] userSelectedItems = new String[4];
    String[] userItems = new String[20];

    //유저 주사위 랜덤
    int userdiceRnd;

    public void onWindowFocusChanged(boolean hasFocus) {

        //enemyHP = imageView.getWidth(); //적군 에너지가 정해지지 않을 경우
        /*enemyHPgauge = imageView.getWidth(); //적군 에너지바의 값을 저장
        userHPgauge = imageView2.getWidth();  //아군 에너지바의 값을 저장*/

        if (!init) {
            ScalableLayout.LayoutParams s1 = scalalo.getChildLayoutParams(enemyHpImgView);
            maxenemyView = s1.getScale_Width();
            enemyViewtop = s1.getScale_Top();
            enemyViewleft = s1.getScale_Left();
            enemyViewheight = s1.getScale_Height();

            s1 = scalalo.getChildLayoutParams(heroHpImgView);
            maxusermyView = s1.getScale_Width();
            userViewtop = s1.getScale_Top();
            userViewleft = s1.getScale_Left();
            userViewheight = s1.getScale_Height();



            userlimitHP = s1.getScale_Width();
            init = true;


            Log.i("유저체력바길이", "" + maxusermyView);

            //유저 현재 체력량 게이지바에 출력
            ScalableLayout.LayoutParams params2 = new ScalableLayout.LayoutParams(userViewleft, userViewtop, (maxusermyView * userHP / userMaxHp), userViewheight);
            heroHpImgView.setLayoutParams(params2);

        }

    }//onWindowFocusChanged


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

//        Toast.makeText(this, DBUserInfo.userInfo.getMaxHp() + "", Toast.LENGTH_SHORT).show();

        //유저 최대체력과 현재체력 값을 동기화
        userMaxHp = DBUserInfo.userInfo.getMaxHp();
        userHP = DBUserInfo.userInfo.getHpParseInt();  //현재 체력 전투 종료시 DB에 저장할 것
        saveMaxHP = userMaxHp;
        saveHP = userHP;


//        "empty", "portion"
        settingPortion = DBUserInfo.userInfo.getSettingPortion();
//        "bundle1", "whip", "whip2", "whip3"
        settingWhip = DBUserInfo.userInfo.getSettingWhip();


        Intent i = getIntent();
        act = i.getIntExtra("stage", 0);
        elite = i.getBooleanExtra("elite", false);
        test_chance = i.getIntExtra("chance", Archive.CHANCE_TO_GET_KEY_ELITE);
        context = this;


        sm = new SoundManager(context);
        sm.initSoundPool();

        //거미, 미친년, 미라, 원주민, 뱀, 군인
        monsterSound = new int[]{sm.SOUND_MONSTER_SPIDER, sm.SOUND_MONSTER_CRAZY, sm.SOUND_MONSTER_MUMMY,
                sm.SOUND_MONSTER_NATIVE, sm.SOUND_MONSTER_SNAKE, sm.SOUND_MONSTER_SOLDIER};

        scalalo = (ScalableLayout) findViewById(R.id.framelayout);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);  //두 번 검색 필요?!
        display = getWindowManager().getDefaultDisplay();
        //width = display.getWidth();
        enemyHpImgView = (ImageView) findViewById(R.id.imageView);
        heroHpImgView = (ImageView) findViewById(R.id.imageView2);
        enemyimage = (ImageView) findViewById(R.id.enemyimage);
        dealtxt = (TextView) findViewById(R.id.dealtxt);
        user_img = (ImageView) findViewById(R.id.user_img);
        enemy_img = (ImageView) findViewById(R.id.enemy_img);
        diceimage = (ImageView) findViewById(R.id.diceimage);
        attackImage = (ImageView) findViewById(R.id.attackImage);
        enemydiceimage = (ImageView) findViewById(R.id.enemydiceimage);

        if(!oncreateinit){
            turn = 0;
            heroDice = 0;
            enemyDice = 0;
            actioncount= 0;
            oncreateinit = true;
        }

        if (diecount != 0) {
            ScalableLayout.LayoutParams params2 =
                    new ScalableLayout.LayoutParams(
                            userViewleft, userViewtop, maxusermyView, userViewheight);
            heroHpImgView.setLayoutParams(params2);
        }


        //채찍 이미지 세팅
        if (!settingWhip.equals("")) {
            switch (settingWhip) {
                case "bundle1":
                    attackImage.setBackgroundResource(R.mipmap.whip1);
                    attackdamage = 25;
                    break;
                case "whip":
                    attackImage.setBackgroundResource(R.mipmap.whip2);
                    attackdamage = 35;
                    break;
                case "whip2":
                    attackImage.setBackgroundResource(R.mipmap.whip3);
                    attackdamage = 45;
                    break;
                case "whip3":
                    attackImage.setBackgroundResource(R.mipmap.whip4);
                    attackdamage = 55;
                    break;
                default:
                    attackdamage = 1;
                    break;
            }
        }
        defenceImage = (ImageView) findViewById(R.id.defenceImage);

        position = (ImageView) findViewById(R.id.position);
        emptyposition = (ImageView) findViewById(R.id.emptyposition);

        if (settingPortion.equals("portion")) {
            position.setVisibility(View.VISIBLE);
        } else {
            emptyposition.setVisibility(View.VISIBLE);
        }

        enemy_border = (ImageView) findViewById(R.id.enemy_border);
        user_border = (ImageView) findViewById(R.id.user_border);

        monster_selRnd = new Random().nextInt(6);
        MonsterInfo monster = new MonsterInfo();


//        enemy_img.setBackgroundResource(R.drawable.enemy_action);
        Log.i("스테이지 정보", ""+act);
        Log.i("중간보스 정보",""+elite);
        switch (act) {

            case 0:
                enemy_img.setBackgroundResource(monster.getLowMonsterImages(monster_selRnd));
                vibration_enemy = (AnimationDrawable) enemy_img.getBackground();
                if (elite == false) {
                    enemyMaxHP = monster.getAct1LowLevelHp();
                    enemyHP = monster.getAct1LowLevelHp();
                } else {
                    enemy_img.setBackgroundResource(monster.getLowMonsterImages(5));
                    vibration_enemy = (AnimationDrawable) enemy_img.getBackground();
                    enemyMaxHP = monster.getAct1MiddleLevelHp();
                    enemyHP = monster.getAct1MiddleLevelHp();
                }
                break;
            case 1:
                enemy_img.setBackgroundResource(monster.getMiddleMonsterImages(monster_selRnd));
                vibration_enemy = (AnimationDrawable) enemy_img.getBackground();

                if (elite == false) {
                    enemyMaxHP = monster.getAct2LowLevelHp();
                    enemyHP = monster.getAct2LowLevelHp();

                } else {
                    enemy_img.setBackgroundResource(monster.getMiddleMonsterImages(5));
                    vibration_enemy = (AnimationDrawable) enemy_img.getBackground();
                    enemyMaxHP = monster.getAct2MiddleLevelHp();
                    enemyHP = monster.getAct2MiddleLevelHp();
                }
                break;
            case 2:
                enemy_img.setBackgroundResource(monster.getHighMonsterImages(monster_selRnd));
                vibration_enemy = (AnimationDrawable) enemy_img.getBackground();
                if (elite == false) {
                    enemyMaxHP = monster.getAct3LowLevelHp();
                    enemyHP = monster.getAct3LowLevelHp();
                } else {
                    enemy_img.setBackgroundResource(monster.getHighMonsterImages(5));
                    vibration_enemy = (AnimationDrawable) enemy_img.getBackground();
                    enemyMaxHP = monster.getAct3MiddleLevelHp();
                    enemyHP = monster.getAct3MiddleLevelHp();
                }

                break;

        }
        Log.i("적 체력",""+enemyMaxHP);
        user_img.setBackgroundResource(R.drawable.hero_ani);
        vibration_user = (AnimationDrawable) user_img.getBackground();

        //플레이어 체력 출력
        userHPtxt = (TextView) findViewById(R.id.userHPtxt);
        userHPtxt.setText("" + (int) userMaxHp);
        userNowHPtxt = (TextView) findViewById(R.id.userNowHPtxt);
        userNowHPtxt.setText("" + (int) userHP);

        //적과 유저 이미지
//        enemy_img.setBackgroundResource(R.drawable.enemy_action);


        user_img.setBackgroundResource(R.drawable.hero_ani);


        //주사위 이미지

        enemydiceimage.setBackgroundResource(R.drawable.enemy_dice);
        enemy_dice = (AnimationDrawable) enemydiceimage.getBackground();

        //적이 맞았을때 표현

        //유저 선공권
        user_border.setVisibility(View.VISIBLE);
        //공격 버튼
        attackImage.setOnClickListener(new View.OnClickListener() {

                                           @Override
                                           public void onClick(View v) {
                                               diceimage.setBackgroundResource(R.drawable.animation_dice);
                                               ani_dice = (AnimationDrawable) diceimage.getBackground();

                                               if (diceCount == 0) {
                                                   diceCount++;
                                                   if (vibration_enemy.isRunning() == true || vibration_user.isRunning() == true) {
                                                       vibration_user.stop();
                                                       vibration_enemy.stop();
                                                   }

                                                   ani_dice.start();
                                                   enemy_dice.start();
                                                   enemydiceimage.setVisibility(View.VISIBLE);


                                               }
                                               else
                                               {
                                                   diceCount = 0;
                                                   ani_dice.stop();
                                                   enemy_dice.stop();
                                                   enemydiceimage.setVisibility(View.INVISIBLE);
                                                   enemyDice = new Random().nextInt(6) + 1;
                                                   heroDice = new Random().nextInt((4 +act)) + (3-act);
                                                   Log.i("적 주사위 값",""+enemyDice);
                                                   Log.i("유저 주사위 값",""+heroDice);
                                                   //for (int i = 0; i < ani_dice.getNumberOfFrames(); i++)
                                                   {

                                                       if (enemyDice == 1) {
                                                           enemyimage.setBackgroundResource(R.drawable.normal_dice01);
                                                       } else if (enemyDice == 2) {
                                                           enemyimage.setBackgroundResource(R.drawable.normal_dice02);
                                                       } else if (enemyDice == 3) {
                                                           enemyimage.setBackgroundResource(R.drawable.normal_dice03);
                                                       } else if (enemyDice == 4) {
                                                           enemyimage.setBackgroundResource(R.drawable.normal_dice04);
                                                       } else if (enemyDice == 5) {
                                                           enemyimage.setBackgroundResource(R.drawable.normal_dice05);
                                                       } else if (enemyDice == 6) {
                                                           enemyimage.setBackgroundResource(R.drawable.normal_dice06);
                                                       }
                                                       enemyimage.setVisibility(View.VISIBLE);
                                                       if (heroDice == 1) {
                                                           diceimage.setBackgroundResource(R.drawable.dice01);
                                                       } else if (heroDice == 2) {
                                                           diceimage.setBackgroundResource(R.drawable.dice02);
                                                       } else if (heroDice == 3) {
                                                           diceimage.setBackgroundResource(R.drawable.dice03);
                                                       } else if (heroDice == 4) {
                                                           diceimage.setBackgroundResource(R.drawable.dice04);
                                                       } else if (heroDice == 5) {
                                                           diceimage.setBackgroundResource(R.drawable.dice05);
                                                       } else if (heroDice == 6) {
                                                           diceimage.setBackgroundResource(R.drawable.dice06);

                                                       }
                                                       diceimage.setVisibility(View.VISIBLE);
                                                       //if (ani_dice.getFrame(i) == ani_dice.getCurrent())
                                                       {
                                                       //    heroDice = i + 1;


                                                           if (turn == 0) { //턴에 관련된 설정
                                                               if (heroDice >= enemyDice) {
                                                                   enemyHP = enemyHP - (heroDice * attackdamage);
                                                                   heroAttDamage = maxenemyView * (enemyHP / enemyMaxHP);
                                                                   ScalableLayout.LayoutParams params =
                                                                           new ScalableLayout.LayoutParams(enemyViewleft
                                                                                   ,enemyViewtop , heroAttDamage, enemyViewheight);

                                                                   enemyHpImgView.setLayoutParams(params);
                                                                   vibration_enemy.setOneShot(true);
                                                                   vibration_enemy.start();

                                                                   //적의 사운드 출력
                                                                   if(elite == true){
                                                                       sm.playSound(monsterSound[5]);
                                                                       Log.i("중간보스일때", "" + monsterSound[5]);
                                                                   }else{
                                                                       Log.i("소리", "" + monster_selRnd);
                                                                       sm.playSound(monsterSound[monster_selRnd]);

                                                                   }

                                                               } else {
                                                                   user_border.setVisibility(View.INVISIBLE);
                                                                   enemy_border.setVisibility(View.VISIBLE);
                                                                   turn = 1;
                                                                   //break;

                                                               }
                                                           }//turn
                                                           else if (turn == 1) { //턴에 관련된 설정
                                                               if (enemyDice > heroDice) {

                                                                   userHP -= (enemyDice * 20);
                                                                   enemyAttDamage = maxusermyView * (userHP / userMaxHp);
                                                                   ScalableLayout.LayoutParams params2 =
                                                                           new ScalableLayout.LayoutParams(
                                                                                   userViewleft, userViewtop, enemyAttDamage, userViewheight);
                                                                   heroHpImgView.setLayoutParams(params2);
                                                                   userNowHPtxt.setText("" + (int) userHP);
                                                                   vibration_user.setOneShot(true);
                                                                   vibration_user.start();

                                                               } else {
                                                                   enemy_border.setVisibility(View.INVISIBLE);
                                                                   user_border.setVisibility(View.VISIBLE);
                                                                   turn = 0;
                                                                   //break;

                                                               }
                                                           }//else if
                                                       }
                                                   }//for

                                                   if (heroAttDamage <= 0 || enemyAttDamage <= 0) {

                                                       if (enemyAttDamage <= 0) {
                                                           diecount++;
                                                           DBUserInfo.userInfo.setMaxHp((int) saveMaxHP);
                                                           DBUserInfo.userInfo.setHp((int) saveMaxHP);
                                                           userMaxHp = DBUserInfo.userInfo.getMaxHp();
                                                           userHP = DBUserInfo.userInfo.getMaxHp();
                                                           userNowHPtxt.setText("0");

                                                       }


                                                       //전투 종료 다이얼로그
                        /*
                        actionend = new Dialog(ActionActivity.this);
                        actionend.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        actionend.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        actionend.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        actionend.setContentView(R.layout.finish_dialog);
                        moneyTxt = (TextView) actionend.findViewById(R.id.moneyTxt);
                        resultTxt = (TextView) actionend.findViewById(R.id.resulttxt);
                        getItemView = (ImageView) actionend.findViewById(R.id.getItemView);




                        if (heroAttDamage <= 0) {
                            getItemView.setBackgroundResource(R.mipmap.gold);
                            resultTxt.setText("승리");
                            Log.i("finish", "승리");
                        } else {
                            getItemView.setBackgroundResource(R.mipmap.action_lose);
                            resultTxt.setText("패배");
                            Log.i("finish", "패배");
                        }
                        getItemView.setClickable(true);
                        getItemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("finish", "클릭");
                                Toast.makeText(ActionActivity.this, "클릭클릭클릭", Toast.LENGTH_SHORT).show();
                                DBUserInfo.userInfo.setHp((int) userHP); //DB에 현재 HP 저장
                                (ActionActivity.this).finish();
                            }
                        });
                        actionend.setCancelable(false);


                        actionend.show();
                        */


                                                       end();

                                                       Log.i("finish", "111111");
                                                   }
                                               }
                                           }
                                       }

        );

        /*
        defenceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        //주사위 버튼
        diceimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */

        //포션 버튼
        position.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            if (userHP >= userMaxHp) {
                                                Toast.makeText(ActionActivity.this,
                                                        "체력이 가득차 있는 상태입니다.",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {

                                                drink = (userlimitHP / 2);
                                                enemyAttDamage = (maxusermyView * (userHP / userMaxHp)) + drink;
//                    userHP = userHP + (int)enemyAttDamage;
                                                userHP = userHP + (DBUserInfo.userInfo.getMaxHp() / 2);

                                                if (userHP >= DBUserInfo.userInfo.getMaxHp()) {
                                                    userHP = DBUserInfo.userInfo.getMaxHp();
                                                }

                                                if (enemyAttDamage > userlimitHP) {
                                                    ScalableLayout.LayoutParams params2 =
                                                            new ScalableLayout.LayoutParams(
                                                                    userViewleft, userViewtop, maxusermyView, userViewheight);
                                                    heroHpImgView.setLayoutParams(params2);
                                                    position.setVisibility(View.INVISIBLE);
                                                    emptyposition.setVisibility(View.VISIBLE);

                                                    //세팅 아이템 해제
                                                    userSetItems = DBUserInfo.userInfo.getSetItems();
                                                    userSetItems[2] = "empty";

                                                    //선택된 영역 해제
                                                    userSelectedItems = DBUserInfo.userInfo.getSelectedItems();

                                                    //실제 아이템1-20창에서 해제
                                                    userItems = DBUserInfo.userInfo.getItems();
                                                    userItems[Integer.parseInt(userSelectedItems[2])] = "empty";

                                                    //2016.04.29
                                                    int potionItemLocation = Integer.parseInt(userSelectedItems[2]);

                                                    for (int i = 0; i < userSelectedItems.length; i++) {
                                                        if (!userSelectedItems[i].equals("empty")) {

                                                            int check = Integer.parseInt(userSelectedItems[i]);

                                                            if (potionItemLocation <= check) {
                                                                userSelectedItems[i] = (check - 1) + "";
                                                            }
                                                        }
                                                    }

                                                    userSelectedItems[2] = "empty";

                                                    refreshItems();


                                                } else {
                                                    ScalableLayout.LayoutParams params2 =
                                                            new ScalableLayout.LayoutParams(
                                                                    userViewleft, userViewtop, enemyAttDamage, userViewheight);
                                                    heroHpImgView.setLayoutParams(params2);
//                        userNowHPtxt.setText("" + (int) userHP);
                                                    position.setVisibility(View.INVISIBLE);
                                                    emptyposition.setVisibility(View.VISIBLE);

                                                    //세팅 아이템 해제
                                                    userSetItems = DBUserInfo.userInfo.getSetItems();
                                                    userSetItems[2] = "empty";

                                                    //선택된 영역 해제
                                                    userSelectedItems = DBUserInfo.userInfo.getSelectedItems();

                                                    //실제 아이템1-20창에서 해제
                                                    userItems = DBUserInfo.userInfo.getItems();
                                                    userItems[Integer.parseInt(userSelectedItems[2])] = "empty";


                                                    //2016.04.29
                                                    int potionItemLocation = Integer.parseInt(userSelectedItems[2]);

                                                    for (int i = 0; i < userSelectedItems.length; i++) {
                                                        if (!userSelectedItems[i].equals("empty")) {

                                                            int check = Integer.parseInt(userSelectedItems[i]);

                                                            if (potionItemLocation <= check) {
                                                                userSelectedItems[i] = (check - 1) + "";
                                                            }
                                                        }
                                                    }
                                                    userSelectedItems[2] = "empty";
                                                    refreshItems();

                                                }
                                            }
                                            DBUserInfo.userInfo.setHp((int) userHP + "");
                                            userNowHPtxt.setText(DBUserInfo.userInfo.getHp());
                                        }

                                    }

        );

        /*getItemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("finish","클릭");
                Toast.makeText(ActionActivity.this, "클릭클릭클릭", Toast.LENGTH_SHORT).show();
                DBUserInfo.userInfo.setHp((int) userHP); //DB에 현재 HP 저장
                (ActionActivity.this).finish();
                return false;
            }
        });*/




    }//onCreate

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                break;

            case KeyEvent.KEYCODE_VOLUME_UP:
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;

        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void end() {
        int rewardMoney = generateMoney();
        Bundle args = new Bundle();
        args.putInt("rewardMoney", rewardMoney);
        args.putFloat("heroAttDamage", heroAttDamage);
        args.putBoolean("elite",elite);
        args.putInt("chance", test_chance);
        args.putInt("userHP", (int) userHP);
        Intent i = new Intent(ActionActivity.this, MainActivity.class);
        i.putExtras(args);
        setResult(RESULT_OK, i);
        finish();
    }

    public int generateMoney() {
        int rewardmoney = 0;
            switch (act) {
                case 0:
                    rewardmoney = new Random().nextInt(10) + 170;
                    break;
                case 1:
                    rewardmoney = new Random().nextInt(20) + 530;
                    break;
                case 2:
                    rewardmoney = new Random().nextInt(50) + 1200;
                    break;
            }
        return rewardmoney;
    }

    /*View.OnClickListener click = new View.OnClickListener() {
    public void onClick(View v) {


        exitNum++;

        if (exitNum >= 2) {
            dialog.dismiss();
            exitNum = 0;
            return;
        }

        handler.removeMessages(0);

        //플레이어 주사위 숫자 난수로 설정
        for (int i = 0; i < dice.length; i++) {
            dice[i].setVisibility(View.INVISIBLE);  //모든 주사위 감춤
        }

        int playerNum = new Random().nextInt(6);
        dice[playerNum].setVisibility(View.VISIBLE);    //난수 해당 주사위만 보여줌
        //플레이어 주사위 숫자 난수로 설정(완)


        for (int i = 0; i < dice.length; i++) {
            if (dice[i].getVisibility() == View.VISIBLE) {
                heroDice = i + 1;
                enemyDice = new Random().nextInt(6) + 1;
                dealtxt.setText((int) heroDice + "의 데미지");
                enemytxt.setText((int) enemyDice + "발생");
            }
        }//for() 주사위 값에 따라 유져와 적 데미지 출력


        if (turn == 0) {        //공격턴이면
            if (heroDice > enemyDice) {
                handler.removeMessages(0);

                enemyLoseHP += heroDice;
                enemyNowHP = enemyHPgauge * (1.0f - (heroDice / (float) enemyHP)); // 감소할 hp게이지 비율
                Toast.makeText(getApplicationContext(), "1:" + enemyNowHP, Toast.LENGTH_SHORT).show();
                ScalableLayout.LayoutParams params = new ScalableLayout.LayoutParams(150, 145, enemyNowHP, 30);
                imageView.setLayoutParams(params);


                if (enemyLoseHP >= enemyHP) {
                    attacktxt.setText("적에게" + (int) (heroDice - enemyDice) + "의 데미지");
                    enemy_img.setBackgroundResource(R.drawable.enemy_action);
                    vibration_enemy = (AnimationDrawable) enemy_img.getBackground();
                    vibration_enemy.setOneShot(true);
                    vibration_enemy.start();
                }

            }//if()
            else {
                attacktxt.setText("실패!");
                turn = 1;
            }

        } else if (turn == 1) {     //방어턴이면
            if (enemyDice > heroDice) {
                Log.i("MY", "check");
                handler.removeMessages(0);
//                                        LinearLayout.LayoutParams params2 =
//                                                new LinearLayout.LayoutParams(
//                                                        imageView2.getWidth() - (enemyDice - heroDice) * 30, imageView.getHeight());
//                                        imageView2.setLayoutParams(params2);

                userNowHP = userHPgauge * (1 - (heroDice / (float) userHP)); // 감소할 hp게이지 비율
                userLoseHP += heroDice;
                Log.i("USER", "" + userLoseHP);
                Toast.makeText(getApplicationContext(), "2:" + userNowHP, Toast.LENGTH_SHORT).show();
                ScalableLayout.LayoutParams params2 = new ScalableLayout.LayoutParams(440, 575, userNowHP, 30);
                imageView2.setLayoutParams(params2);
                float checkHP = userHP - userLoseHP;
                userNowHPtxt.setText("" + (int) checkHP);


                if (userLoseHP >= userHP) {
                    attacktxt.setText((int) (enemyDice - heroDice) + "에게 데미지를 받습니다.");
                    user_img.setBackgroundResource(R.drawable.user_action);
                    vibration_user = (AnimationDrawable) user_img.getBackground();
                    vibration_user.setOneShot(true);
                    vibration_user.start();
                    finish();
                }
            }//if()
            else {
                attacktxt.setText("공격!");
                turn = 0;

            }
        }


    }//onClick()
};//click


    private void visible(int d) {

        dice[d].setVisibility(View.VISIBLE);

        for (int i = 0; i < dice.length; i++) {
            if (i != d) {
                dice[i].setVisibility(View.INVISIBLE);
            }
        }

    }//visible


    //다이스 shake
    private void shake() {
        count++;
        rot = count % 6;
        visible(rot);

        if (count == 6) {
            count = 0;
        }
    }//shake*/


    /*private void gauge() {

//        Log.i("HP", ""+enemyHP);
        if ((int) userLoseHP >= userHP || (int) enemyLoseHP >= enemyHP) {
            attacktxt2.setText("전투가 종료됩니다.");
            dialog.dismiss();
            finish();
        }

    }//gauge*/


//    Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//        /*shake();
//        gauge();*/
//            handler.sendEmptyMessageDelayed(0, 350);//주사위 핸들러
//
//
////            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageView.getWidth() - heroDice*5, imageView.getHeight());
////            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(imageView2.getWidth() - 20, imageView.getHeight());
////            imageView.setLayoutParams(params);
////            imageView2.setLayoutParams(params2);
//
//        }
//    };


    public void refreshItems() {
        //아이템 판매할 경우 아이템 창 새로고침
        //아이템 정보를 새로 담을 그릇
        String refreshItems[] = new String[20];
        int cnt = 0;


        //empty가 아니면 담아라
        String[] userDbItem = new String[20];
        userDbItem = DBUserInfo.userInfo.getItems();
        for (int i = 0; i < userDbItem.length; i++) {
            if (!userDbItem[i].equals("empty")) {
                refreshItems[cnt] = userDbItem[i];
                cnt++;
            }
        }

        //빈공간 모드 empty로 채우기
        for (int i = cnt; i < userDbItem.length; i++) {
            refreshItems[i] = "empty";
        }

        //UserInfo에 세팅
        DBUserInfo.userInfo.setItems(refreshItems);
    }




}//classEnd
