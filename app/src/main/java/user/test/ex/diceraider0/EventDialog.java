package user.test.ex.diceraider0;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import user.test.ex.db.DBUserInfo;

/**
 * Created by INC-B-05 on 2016-04-05.
 */

/* Hogan 2016-04-18
 * 각 이벤트 동작 메서드로 분리
 * doEventPlain(), doEventTreasure(), doEventTrap(), doEventHouse(), doEventAntagonist(), doEventBoss()
 * mode 삭제(불필요, 미사용)
 */

public class EventDialog extends Dialog implements Archive{

    Context context;

    WallView wallView;

    AnimationDrawable ani;

    BitmapCentral b;

    //Dice 관련
    ImageView diceImg;
    Dice dice;
    boolean stopDice; // true: 주사위 멈춤 / false: dice 돌아감

    private boolean diceEnd;
    private boolean stageEnd;
    private boolean stageRestart;

    public EventDialog(Context context, WallView wallView) {
        super(context);
        this.context = context;
        this.wallView = wallView;

        b = new BitmapCentral(context, "EventDialog");
        b.createDiceBmp();
        dice = new Dice();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
    } // constructor


    public void showDice() {

        setContentView(R.layout.dialog_dice);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        diceImg = (ImageView)findViewById(R.id.diceImg);
        diceImg.setBackgroundResource(R.drawable.animation_dice);
        diceImg.setOnTouchListener(touch_dice);
        ani = (AnimationDrawable)diceImg.getBackground();

        stopDice = false;

        ani.start();
        show();
        //diceAnimation

    } // showDice()


    View.OnTouchListener touch_dice = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (!stopDice){
                ani.stop();
                stopDice = true;

                dice.roll(MAX_6);

                diceImg.setImageBitmap(b.getDiceBmp(dice.getDiceResult()));
            } else {
                dismiss();
                diceEnd = true;
                wallView.setNum_move(dice.getDiceResult());
                wallView.move();
            }

            return false;
        }
    };


    public void showReward(String rewardItem, int money, final int stage) {
        // 보상에 대한 dialog
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_reward);
        setCancelable(false);

        TextView txtReward = (TextView)findViewById(R.id.txtReward);
        txtReward.setText("보상 아이템 " + rewardItem + "%n골드" + money);
        Button btnRewardOK = (Button)findViewById(R.id.btnRewardOK);
        btnRewardOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                showEndStage(stage + 1);
            }
        });

        show();
    }

    public void showEndStage(int next_stage) {
        // 스테이지 변동에 대한 dialog
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_end_stage);
        setCancelable(false);

        Button btnStageEnd, btnStageNO;

        btnStageEnd = (Button)findViewById(R.id.btnStageEnd);
        btnStageEnd.setText("Stage" + next_stage);
        btnStageEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageEnd = true;
                dismiss();
            }
        });

        btnStageNO = (Button)findViewById(R.id.btnStageNo);
        btnStageNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageRestart = true;
                dismiss();
            }
        });

        show();
    }

    public void showRestartStage() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_restart);
        setCancelable(false);

        Button btnRestartOK = (Button)findViewById(R.id.btnRestartOK);
        btnRestartOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageRestart = true;
                dismiss();
            }
        });

        show();
    }


    public boolean eventCatch(int infoTile, int currStage){
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
                doEventHouse();
                break;

            case ANTAGONIST:
                doEventAntagonist();
                break;

            case BOSS:
                doEventBoss(currStage);
                break;

            case INVALID:
            default:
                return false;
        }

        return true;
    }


    private void doEventPlain() {
        //잉여칸: 현재 적 이벤트로 연결
        doEventAntagonist();
    } // doEventPlain


    private void doEventTreasure() {

        //돈 다이얼로그
        // requestWindowFeature(Window.FEATURE_NO_TITLE);  //타이틀 바 제거
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //기본 배경 제거

        setContentView(R.layout.dialog_money);

        Button dialogMoneyBtn = (Button)findViewById(R.id.dialogMoneyBtn);
        TextView moneyTxt = (TextView)findViewById(R.id.moneyTxt);

        //돈 랜덤하게 제공
        int getMoney = (new Random().nextInt(5)+1)*100;
        moneyTxt.setText("+" + getMoney);

//        money += getMoney;      //사용자 돈 변경
//        myMoneyTxt.setText(""+money);  //메인에서 사용자 돈 출력

        int intMoney = DBUserInfo.userInfo.getMoneyParseInt();
        int nowMoney = intMoney + getMoney;
        DBUserInfo.userInfo.setMoney(nowMoney);


        dialogMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                playerPosition += 1; //플레이어 위치 1씩 증가
                dismiss();
            }
        });

        setCancelable(false); // 버튼 클릭 없이 종료 불가(false)
        show();
    } // doEventTreasure


    private void doEventTrap() {

        //함정 다이얼로그
        // requestWindowFeature(Window.FEATURE_NO_TITLE);  //타이틀 바 제거
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //기본 배경 제거

        setContentView(R.layout.dialog_trap);

        Button dialogTrapBtn = (Button)findViewById(R.id.dialogTrapBtn);
        TextView trapTxt = (TextView)findViewById(R.id.trapTxt);

        //체력 랜덤하게 제공
        int getHealth = (new Random().nextInt(5)+1)*50;
        trapTxt.setText("-" + getHealth);
//        health -= getHealth;      //사용자 체력 변경
//        healthTxt.setText(""+ health);  //메인에서 사용자 돈 출력

        String hp = DBUserInfo.userInfo.getHp();
        int intHp = Integer.parseInt(hp);
        int nowHp = intHp - getHealth;
        DBUserInfo.userInfo.setHp(nowHp);


        dialogTrapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(health <= 0){
//                    Toast.makeText(getApplicationContext(), "플레이어가 죽었습니다!", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                playerPosition += 1; //플레이어 위치 1씩 증가
                dismiss();
//                Toast.makeText(context, "" + DBUserInfo.userInfo.getHp(), Toast.LENGTH_SHORT).show();
            }
        });

        setCancelable(false); // 버튼 클릭 없이 종료 불가(false)
        show();

    } // doEventTrap


    private void doEventHouse() {

        Intent intent = new Intent( context, PlainActivity.class );  //PlainActivity로 연결
        context.startActivity(intent);

    } // doEventHouse()


    private void doEventAntagonist() {

        Intent intent = new Intent( context, ActionActivity.class );  //ActionActivity로 연결
        intent.putExtra("stage" , wallView.getCurrStage());
        context.startActivity(intent);

    } // doEventAntagonist()


    private void doEventBoss(int currStage) {

        Intent intent;
        Bundle bundle = new Bundle();

        if (currStage == StageInfo.STAGE_THREE) {
            intent = new Intent( context, ArrowBossActivity.class );
            // 콜백 인텐트
            ((Activity)context).startActivityForResult(intent ,RETURN_ARROW_BOSS );
        } else if (currStage == StageInfo.STAGE_TWO){
            intent = new Intent (context, BossActivity.class); //BossActivity로 연결
            intent.putExtras(bundle);
            bundle.putBoolean("knife", true);
            ((Activity)context).startActivityForResult(intent, RETURN_BOSS_JUNGLE);
        } else {
            intent = new Intent (context, KeyActivity.class); //Boss로 연결
            intent.putExtras(bundle);
            ((Activity)context).startActivityForResult(intent, RETURN_BOSS_TOMB_KEY);
        }

    } // doEventBoss()

    public boolean isDiceEnd() {
        return diceEnd;
    }

    public void setDiceEnd(boolean diceEnd) {
        this.diceEnd = diceEnd;
    }

    public boolean isStageEnd() {
        return stageEnd;
    }

    public void setStageEnd(boolean stageEnd) {
        this.stageEnd = stageEnd;
    }

    public boolean isStageRestart() {
        return stageRestart;
    }

    public void setStageRestart(boolean stageRestart) {
        this.stageRestart = stageRestart;
    }

    public void remove() {
        b.remove();
    }


    //old codes
    /*
    private void doEventTreasureOld() {

        final Dialog dialog = new Dialog(context);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  //타이틀 바 제거
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //기본 배경 제거

        dialog.setContentView(R.layout.dialog_money);

        Button dialogMoneyBtn = (Button)dialog.findViewById(R.id.dialogMoneyBtn);
        TextView moneyTxt = (TextView)dialog.findViewById(R.id.moneyTxt);

        //돈 랜덤하게 제공
        int getMoney = (new Random().nextInt(5)+1)*100;
        moneyTxt.setText("+" + getMoney);

//                money += getMoney;      //사용자 돈 변경
//                myMoneyTxt.setText(""+money);  //메인에서 사용자 돈 출력

        String money = DBUserInfo.userInfo.getHp();
        int intMoney = Integer.parseInt(money);
        int nowMoney = intMoney - getMoney;
        DBUserInfo.userInfo.setMoney(""+nowMoney);


        dialogMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        playerPosition += 1; //플레이어 위치 1씩 증가
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false); // 버튼 클릭 없이 종료 불가(false)
        dialog.show();

    }

    private void doEventTrapOld() {

        final Dialog dialogTrap = new Dialog(context);

        dialogTrap.requestWindowFeature(Window.FEATURE_NO_TITLE);  //타이틀 바 제거
        dialogTrap.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //기본 배경 제거

        dialogTrap.setContentView(R.layout.dialog_trap);

        Button dialogTrapBtn = (Button)dialogTrap.findViewById(R.id.dialogTrapBtn);
        TextView trapTxt = (TextView)dialogTrap.findViewById(R.id.trapTxt);

        //체력 랜덤하게 제공
        int getHealth = (new Random().nextInt(5)+1)*10;
        trapTxt.setText("-" + getHealth);
//                health -= getHealth;      //사용자 체력 변경
//                healthTxt.setText(""+ health);  //메인에서 사용자 돈 출력

        String hp = DBUserInfo.userInfo.getHp();
        int intHp = Integer.parseInt(hp);
        int nowHp = intHp - getHealth;
        DBUserInfo.userInfo.setHp(""+nowHp);


        dialogTrapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        if(health <= 0){
//                            Toast.makeText(getApplicationContext(), "플레이어가 죽었습니다!", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                        playerPosition += 1; //플레이어 위치 1씩 증가
                dialogTrap.dismiss();
//                        Toast.makeText(context, ""+DBUserInfo.userInfo.getHp(), Toast.LENGTH_SHORT).show();
            }
        });

        dialogTrap.setCancelable(false); // 버튼 클릭 없이 종료 불가(false)
        dialogTrap.show();

    }
    */

}
