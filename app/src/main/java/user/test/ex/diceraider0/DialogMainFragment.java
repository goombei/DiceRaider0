package user.test.ex.diceraider0;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

import user.test.ex.db.DBUserInfo;

/**
 * Created by INC-B-05 on 2016-04-25.
 */
public class DialogMainFragment extends DialogFragment implements Archive {


    //모드별 layout
    private final int[] ARR_LAYOUT = {
            R.layout.dialog_dice,
            R.layout.dialog_money,
            R.layout.dialog_trap,
            R.layout.dialog_money,
            R.layout.dialog_end_stage,
            R.layout.dialog_restart,
            R.layout.finish_dialog,
            R.layout.dialog_test};

    private int mode;


    //Dice 관련
    private ImageView diceImg;
    private AnimationDrawable ani_dice;

    private boolean stopDice;

    LinearLayout.LayoutParams params;

    //Test 관련


    public interface DialogResultLinker {
        int rollDice();

        void move();

        void rewardOK();

        void stageRestart();

        void stageUp();

        void refreshStatus();

        void setTest(int test_mode, int stage, int chance, boolean thrupass);

        void sendBundle(Bundle bundle);
    }


    public DialogMainFragment() {

    }


    public static DialogMainFragment newInstance(Bundle args) {
        /* Bundle put/get: "mode"(int), "rewardItem"(String), "money"(int), "stage"(int) */
        DialogMainFragment fragment = new DialogMainFragment();

        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mode = getArguments().getInt("mode");
        return inflater.inflate(ARR_LAYOUT[mode], container);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switch (mode) {
            case MODE_DICE:
                showDice(view);
                break;

            case MODE_TREASURE:
                showTreasure(view);
                break;

            case MODE_TRAP:
                showTrap(view);
                break;

            case MODE_REWARD:
                showTreasure(view);
                break;

            case MODE_END_STAGE:
                showEndStage(view);
                break;

            case MODE_RESTART:
                showRestartStage(view);
                break;

            case MODE_END_ACTION:
                showEndAction(view);
                // showTreasure(view);
                break;

            case MODE_TEST:
                showTest(view);
                break;

            default:
                break;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }


    private void showDice(View view) {
        setCancelable(false);

        WindowManager wm = getActivity().getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        params = new LinearLayout.LayoutParams(dm.widthPixels / 3, dm.widthPixels / 3);

        diceImg = (ImageView) view.findViewById(R.id.diceImg);
        diceImg.setLayoutParams(params);
        diceImg.setBackgroundResource(R.drawable.animation_dice);
        ani_dice = (AnimationDrawable) diceImg.getBackground();

        diceImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                DialogResultLinker listener = (DialogResultLinker) getActivity();

                if (stopDice) {
                    listener.move();
                    dismiss();

                } else {
                    int diceResult = listener.rollDice();

                    ani_dice.stop();
                    stopDice = true;

                    // diceImg.setImageBitmap(b.getDiceBmp(diceResult));
                    diceImg.setBackgroundResource(DICE_RES[diceResult - 1]);
                }

                return false;
            }
        });

        ani_dice.start();
        stopDice = false;
    }


    private void showTreasure(View view) {
        //돈 다이얼로그
        setCancelable(false); // 버튼 클릭 없이 종료 불가(false)

        Button dialogMoneyBtn = (Button) view.findViewById(R.id.dialogMoneyBtn);
        TextView moneyTxt = (TextView) view.findViewById(R.id.moneyTxt);

        int getMoney = 0;
        int stage = getArguments().getInt("stage");
        // String rewardItem = getArguments().getString("rewardItem", "");

        if (mode == MODE_TREASURE) {
            int[] bonus = {140, 530, 1200};
            getMoney = new Random().nextInt(50) + bonus[stage];

        } else if (mode == MODE_END_ACTION) {

            getMoney = getArguments().getInt("rewardMoney", 0);
        } else {

        }

//        money += getMoney;      //사용자 돈 변경
//        myMoneyTxt.setText(""+money);  //메인에서 사용자 돈 출력
        String str = String.format(Locale.getDefault(), "+%dG", getMoney);
        moneyTxt.setText(str);

        int intMoney = DBUserInfo.userInfo.getMoneyParseInt();
        int nowMoney = intMoney + getMoney;
        DBUserInfo.userInfo.setMoney(nowMoney);

        dialogMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                playerPosition += 1; //플레이어 위치 1씩 증가
                if (mode == MODE_REWARD) {
                    DialogResultLinker listener = (DialogResultLinker) getActivity();
                    listener.rewardOK();
                }

                dismiss();
            }
        });

    } // doEventTreasure


    private void showTrap(View view) {

        setCancelable(false); // 버튼 클릭 없이 종료 불가(false)

        Button dialogTrapBtn = (Button) view.findViewById(R.id.dialogTrapBtn);
        TextView trapTxt = (TextView) view.findViewById(R.id.trapTxt);

        //체력 랜덤하게 제공
        int getHealth = (new Random().nextInt(5) + 1) * 50;
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

                DialogResultLinker listener = (DialogResultLinker) getActivity();
                listener.refreshStatus();

                dismiss();
//                Toast.makeText(context, "" + DBUserInfo.userInfo.getHp(), Toast.LENGTH_SHORT).show();
            }
        });

    } // doEventTrap


    public void showEndStage(View view) {
        // 스테이지 변동에 대한 dialog
        setCancelable(false);

        final String rewardItem = getArguments().getString("rewardItem", "");
        final int money = getArguments().getInt("rewardMoney");
        final int stage = getArguments().getInt("stage") + 2;

        Button btnStageEnd, btnStageNO;

        btnStageEnd = (Button) view.findViewById(R.id.btnStageEnd);
        String str_stage = (stage > STAGE_MAX) ?
                "Ranking" : String.format(Locale.getDefault(), "Stage%d", stage);
        btnStageEnd.setText(str_stage);

        btnStageEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogResultLinker listener = (DialogResultLinker) getActivity();
                listener.stageUp();
                dismiss();
            }
        });

        btnStageNO = (Button) view.findViewById(R.id.btnStageNo);
        btnStageNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogResultLinker listener = (DialogResultLinker) getActivity();
                listener.stageRestart();
                dismiss();
            }
        });

    }


    public void showRestartStage(View view) {

        setCancelable(false);

        Button btnRestartOK = (Button) view.findViewById(R.id.btnRestartOK);
        btnRestartOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogResultLinker listener = (DialogResultLinker) getActivity();
                listener.stageRestart();
                dismiss();
            }
        });

    }

    public void showEndAction(View view) {

        setCancelable(false);

        TextView moneyTxt, resultTxt;
        Button finishBtn;

        moneyTxt = (TextView) view.findViewById(R.id.moneyTxt);
        resultTxt = (TextView) view.findViewById(R.id.resulttxt);
        finishBtn = (Button) view.findViewById(R.id.finishBtn);
        float heroAttDamage = getArguments().getFloat("heroAttDamage");
        int getMoney = getArguments().getInt("rewardMoney");
        boolean elite = getArguments().getBoolean("elite");
        int chance = getArguments().getInt("chance");

        Toast.makeText(getActivity(), elite + " " + chance, Toast.LENGTH_SHORT).show();


        final boolean idol,
                isWin = heroAttDamage <= 0;
        String str_money, str_result;

        if (isWin) {
            int num = new Random().nextInt(100);
            idol = elite ?
                    num < chance : num < Archive.CHANCE_TO_GET_KEY_NORMAL;

            str_result = idol ?
                    String.format(Locale.getDefault(), "승리, 보스 도전 가능")
                    : "승리";
            str_money = String.format(Locale.getDefault(), "%d", getMoney);

            finishBtn.setBackgroundResource(R.mipmap.dialog_money);
            resultTxt.setTextColor(0xffffd700);

            int intMoney = DBUserInfo.userInfo.getMoneyParseInt();
            int nowMoney = intMoney + getMoney;
            DBUserInfo.userInfo.setMoney(nowMoney);

            Log.i("finish", "승리");
        } else {
            idol = false;
            str_result = "패배";
            str_money = "0";

            finishBtn.setBackgroundResource(R.mipmap.action_lose);

            Log.i("finish", "패배");
        }

        resultTxt.setText(str_result);
        moneyTxt.setText(str_money);


        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("finish", "클릭");
                DialogResultLinker linker = (DialogResultLinker) getActivity();
                if (!isWin) {
                    linker.stageRestart();
                } else {
                    // 보스 조건 전달
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("idol", idol);
                    linker.sendBundle(bundle);
                }

                dismiss();
            }
        });
    }


    public void showTest(View view) {
        setCancelable(false);

        final RadioButton[]
                r_test = new RadioButton[R_RES_TEST.length],
                r_stage = new RadioButton[R_RES_STAGE.length];
        Button btn_ok, btn_cancel;
        final EditText et_chance;
        final CheckBox cb_boss_stage1;

        for (int i = 0; i < r_test.length; i++) {
            r_test[i] = (RadioButton) view.findViewById(R_RES_TEST[i]);
        }
        for (int i = 0; i < r_stage.length; i++) {
            r_stage[i] = (RadioButton) view.findViewById(R_RES_STAGE[i]);
        }

        btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        et_chance = (EditText) view.findViewById(R.id.et_chance);
        cb_boss_stage1 = (CheckBox) view.findViewById(R.id.cb_boss_stage1);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int test_mode = 0, test_stage = 0, test_chance;
                boolean thrupass = cb_boss_stage1.isChecked();
                for (int i = 0; i < r_test.length; i++) {
                    if (r_test[i].isChecked()) {
                        test_mode = i;
                    }
                }
                for (int i = 0; i < r_stage.length; i++) {
                    if (r_stage[i].isChecked()) {
                        test_stage = i;
                    }
                }

                test_chance = et_chance.getText().toString().equals("") ?
                        (test_mode == TEST_ACTION) ?
                                CHANCE_TO_GET_KEY_NORMAL : CHANCE_TO_GET_KEY_ELITE
                        : Integer.parseInt(et_chance.getText().toString());
                test_chance = (test_chance > 100) ?
                        100 : test_chance;



                DialogResultLinker linker = (DialogResultLinker) getActivity();
                linker.setTest(test_mode, test_stage, test_chance, thrupass);

                dismiss();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        et_chance.addTextChangedListener(new TextWatcher() {

            String prevStr = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prevStr = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_chance.getLineCount() > 1) {
                    et_chance.setText(prevStr);
                    et_chance.setSelection(et_chance.length());
                }
            }
        });
    }

    /* 미사용 */
    /*
    public void showReward(View view) {
        // 보상에 대한 dialog
        setCancelable(false);

        final String rewardItem = getArguments().getString("rewardItem","");
        final int money = getArguments().getInt("rewardMoney");
        final int stage = getArguments().getInt("stage");

        TextView txtReward = (TextView)view.findViewById(R.id.txtReward);
        // txtReward.setText("보상 아이템 " + rewardItem + " 골드" + money);
        String str = String.format(Locale.getDefault(), "Reward%n%d G", money);
        txtReward.setText(str);
        Button btnRewardOK = (Button)view.findViewById(R.id.btnRewardOK);
        btnRewardOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogResultLinker listener = (DialogResultLinker) getActivity();
                listener.rewardOK();
                dismiss();
                // showEndStage(stage + 1, view);
            }
        });

    }
    */
}
